const express = require('express');
const path = require('path');
const axios = require('axios');

const app = express();
const PORT = 3000;
const USER_SERVICE_URL = process.env.USER_SERVICE_URL || 'http://127.0.0.1:8081';

app.use(express.urlencoded({ extended: true }));
app.use(express.json());
app.use(express.static(path.join(__dirname, 'public')));

app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

app.get('/verify', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'verify.html'));
});

app.get('/register', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'register.html'));
});

app.get('/dashboard', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'dashboard.html'));
});

app.post('/api/send-code', async (req, res) => {
    const email = req.body.email?.trim();
    if (!email) return res.status(400).json({ message: 'Informe um e-mail válido.' });

    try {
        await axios.post(`${USER_SERVICE_URL}/auth/request-code`, { email }, { timeout: 30000 });
        res.status(200).json({ success: true, message: 'Código enviado com sucesso!' });
    } catch (error) {
        console.error('Erro em /api/send-code:', error.message, error.response?.data || '');
        res.status(error.response?.status || 500).json({ 
            message: error.response?.data?.message || 'User Service indisponível ou erro interno.' 
        });
    }
});

app.post('/api/verify-code', async (req, res) => {
    const email = req.body.email?.trim();
    const code = req.body.code?.trim();

    if (!email || !code) return res.status(400).json({ message: 'Informe e-mail e código.' });

    try {
        const response = await axios.post(`${USER_SERVICE_URL}/auth/verify-code`, { email, code });
        res.status(200).json({ token: response.data.token });
    } catch (error) {
        console.error('Erro em /api/verify-code:', error.message, error.response?.data || '');
        res.status(error.response?.status || 500).json({ 
            message: error.response?.data?.message || 'Código inválido ou expirado.' 
        });
    }
});

app.post('/api/register', async (req, res) => {
    const authHeader = req.headers.authorization;
    if (!authHeader) return res.status(401).json({ message: 'Token não informado.' });

    const { name, role } = req.body;
    if (!name?.trim() || !role) return res.status(400).json({ message: 'Informe nome e cargo.' });

    try {
        await axios.post(
            `${USER_SERVICE_URL}/users/update-profile`,
            { name: name.trim(), role },
            { headers: { Authorization: authHeader }, timeout: 30000 }
        );
        res.status(200).json({ success: true });
    } catch (error) {
        console.error('Erro em /api/register:', error.message, error.response?.data || '');
        res.status(error.response?.status || 500).json({ 
            message: error.response?.data?.message || 'Erro ao atualizar perfil.' 
        });
    }
});

app.get('/api/protected', async (req, res) => {
    const authHeader = req.headers.authorization;
    if (!authHeader) return res.status(401).json({ message: 'Token não informado.' });

    try {
        const response = await axios.get(`${USER_SERVICE_URL}/users/test/customer`, {
            headers: { Authorization: authHeader }
        });
        res.status(response.status).json(response.data);
    } catch (error) {
        console.error('Erro em /api/protected:', error.message, error.response?.data || '');
        res.status(error.response?.status || 500).json(error.response?.data || { message: error.message });
    }
});

app.get('/api/me', async (req, res) => {
    const authHeader = req.headers.authorization;
    if (!authHeader) return res.status(401).json({ message: 'Token não informado.' });

    try {
        const response = await axios.get(`${USER_SERVICE_URL}/users/me`, {
            headers: { Authorization: authHeader }
        });
        res.status(response.status).json(response.data);
    } catch (error) {
        console.error('Erro em /api/me:', error.message, error.response?.data || '');
        res.status(error.response?.status || 500).json(error.response?.data || { message: error.message });
    }
});

app.listen(PORT, '0.0.0.0', () => {
    console.log(`Frontend rodando em http://localhost:${PORT}`);
});