const express = require('express');
const router = express.Router();
const resenasController = require('../controllers/resenasController');

router.post('/', resenasController.crearResena);
router.get('/producto/:idProducto', resenasController.getResenasPorProducto);
router.get('/', resenasController.getTodasResenas);
router.get('/usuario/:idUsuario', resenasController.getResenasPorUsuario);
router.put('/:idResena', resenasController.actualizarResena);
router.delete('/:idResena', resenasController.eliminarResena);

module.exports = router;