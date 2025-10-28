const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
require('dotenv').config();

const db = require('./config/database');
const productosRouter = require('./routes/productos');
const usuariosRouter = require('./routes/usuarios');
const pedidosRouter = require('./routes/pedidos');
const resenasRouter = require('./routes/resenas');
const carritoRouter = require('./routes/carrito');  // ← NUEVO
const referidosRoutes = require('./routes/referidosRoutes');
const direccionesRoutes = require('./routes/direcciones');

const app = express();
const PORT = process.env.PORT || 3001;

// Middleware
app.use(cors());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// Rutas
app.use('/api/productos', productosRouter);
app.use('/api/usuarios', usuariosRouter);
app.use('/api/pedidos', pedidosRouter);
app.use('/api/resenas', resenasRouter);
app.use('/api/carrito', carritoRouter);  // ← NUEVO
app.use('/api', referidosRoutes);
app.use('/api/direcciones', direccionesRoutes);

// Ruta de prueba
app.get('/api/health', (req, res) => {
  res.json({ status: 'OK', message: 'API Level-UP funcionando correctamente' });
});

// Iniciar servidor
async function startServer() {
  try {
    await db.initializePool();
    
    app.listen(PORT, () => {
      console.log(`🚀 Servidor corriendo en http://localhost:${PORT}`);
      console.log(`📊 API disponible en http://localhost:${PORT}/api`);
    });
  } catch (err) {
    console.error('❌ Error al iniciar servidor:', err);
    process.exit(1);
  }
}

// Cerrar conexiones al terminar
process.on('SIGTERM', async () => {
  console.log('SIGTERM recibido, cerrando servidor...');
  await db.closePool();
  process.exit(0);
});

process.on('SIGINT', async () => {
  console.log('SIGINT recibido, cerrando servidor...');
  await db.closePool();
  process.exit(0);
});

startServer();