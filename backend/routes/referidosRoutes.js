const express = require('express');
const router = express.Router();
const referidosController = require('../controllers/referidosController');

// Registrar un nuevo referido
router.post('/referidos', referidosController.registrarReferido);

// Obtener referidos de un usuario
router.get('/referidos/:id_usuario', referidosController.obtenerReferidos);

// Completar un referido
router.put('/referidos/completar', referidosController.completarReferido);

module.exports = router;