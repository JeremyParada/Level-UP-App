const express = require('express');
const router = express.Router();
const direccionesController = require('../controllers/direccionesController');

// Ruta para obtener direcciones de un usuario
router.get('/:idUsuario', direccionesController.getDireccionesPorUsuario);

// Ruta para crear una nueva dirección
router.post('/', direccionesController.createDireccion);

module.exports = router;