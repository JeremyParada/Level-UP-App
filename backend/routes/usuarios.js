const express = require('express');
const router = express.Router();
const usuariosController = require('../controllers/usuariosController');

// Rutas de usuarios
router.post('/registro', usuariosController.registrarUsuario);
router.post('/login', usuariosController.login);
router.get('/:idUsuario', usuariosController.getPerfil);
router.put('/:idUsuario', usuariosController.actualizarPerfil);
router.post('/:idUsuario/puntos', usuariosController.actualizarPuntos);

module.exports = router;