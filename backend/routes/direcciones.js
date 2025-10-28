const express = require('express');
const router = express.Router();
const direccionesController = require('../controllers/direccionesController');

// Rutas para direcciones
router.get('/usuario/:idUsuario', direccionesController.getDireccionesPorUsuario);
router.post('/', direccionesController.createDireccion);

module.exports = router;