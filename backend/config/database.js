const oracledb = require('oracledb');
const path = require('path');
const os = require('os');
require('dotenv').config();

// Detectar el sistema operativo
const platform = os.platform();
let instantClientPath;

// Configurar la ruta del Instant Client según el sistema operativo
if (platform === 'win32') {
  // Ruta para Windows
  instantClientPath = path.join(__dirname, '../../instantclient_windows');
  if (!process.env.PATH.includes(instantClientPath)) {
    process.env.PATH = `${instantClientPath};${process.env.PATH}`;
    console.log(`🔧 Oracle Instant Client añadido al PATH: ${instantClientPath}`);
  }
} else if (platform === 'linux') {
  // Ruta para Linux
  instantClientPath = path.join(__dirname, '../../instantclient_linux');
  console.log(`🔧 Verificando Oracle Instant Client en Linux: ${instantClientPath}`);
} else {
  console.error('❌ Sistema operativo no soportado. Solo Windows y Linux están soportados.');
  process.exit(1);
}

// Configurar wallet de Oracle Cloud
const walletLocation = process.env.TNS_ADMIN || path.join(__dirname, '../../wallet');
const walletPassword = process.env.WALLET_PASSWORD || ''; // Solo si tu wallet tiene password

// Configuración para Oracle Cloud
oracledb.initOracleClient({
  libDir: instantClientPath, // Ruta al Instant Client
  configDir: walletLocation  // Ruta al wallet de Oracle
});

// Configuración de conexión
const dbConfig = {
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD,
  connectString: process.env.DB_CONNECT_STRING,
  // Configuración del pool
  poolMin: 2,
  poolMax: 10,
  poolIncrement: 2,
  poolTimeout: 60,
  // Para Oracle Cloud Autonomous Database
  walletLocation: walletLocation,
  walletPassword: walletPassword
};

// Inicializar el pool de conexiones
async function initializePool() {
  try {
    console.log('🔧 Configurando Oracle Cloud Database...');
    console.log('📁 Wallet location:', walletLocation);
    console.log('🔗 Connect string:', dbConfig.connectString);
    
    await oracledb.createPool(dbConfig);
    
    console.log('✅ Pool de conexiones Oracle Cloud creado exitosamente');
    
    // Probar conexión
    const connection = await oracledb.getConnection();
    const result = await connection.execute('SELECT SYSDATE FROM DUAL');
    console.log('✅ Conexión exitosa. Fecha del servidor:', result.rows[0][0]);
    await connection.close();
    
  } catch (err) {
    console.error('❌ Error al crear pool de conexiones:', err);
    console.error('Detalles:', {
      message: err.message,
      code: err.errorNum,
      offset: err.offset
    });
    process.exit(1);
  }
}

// Cerrar el pool
async function closePool() {
  try {
    await oracledb.getPool().close(10);
    console.log('✅ Pool de conexiones cerrado');
  } catch (err) {
    console.error('❌ Error al cerrar pool:', err);
  }
}

// Ejecutar consultas
async function execute(sql, binds = [], opts = {}) {
  let connection;
  opts.outFormat = oracledb.OUT_FORMAT_OBJECT;
  opts.autoCommit = true;

  try {
    const pool = oracledb.getPool(); // Obtener el pool existente
    connection = await pool.getConnection(); // Obtener una conexión del pool
    const result = await connection.execute(sql, binds, opts);
    return result;
  } catch (err) {
    console.error('❌ Error en consulta:', err);
    throw err;
  } finally {
    if (connection) {
      try {
        await connection.close(); // Cerrar la conexión después de usarla
      } catch (err) {
        console.error('❌ Error al cerrar conexión:', err);
      }
    }
  }
}

module.exports = {
  initializePool,
  closePool,
  execute
};