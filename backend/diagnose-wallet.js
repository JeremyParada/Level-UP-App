const fs = require('fs');
const path = require('path');
require('dotenv').config();

console.log('🔍 Diagnóstico de Wallet Oracle Cloud\n');
console.log('='.repeat(60));

const walletPath = process.env.TNS_ADMIN || path.join(__dirname, '../wallet');
const instantClientPath = process.env.ORACLE_CLIENT_LIB;

// 1. Verificar wallet path
console.log('\n1️⃣ WALLET LOCATION');
console.log('Path configurado:', walletPath);
console.log('¿Existe?', fs.existsSync(walletPath) ? '✅ SI' : '❌ NO');

if (fs.existsSync(walletPath)) {
  // 2. Verificar archivos del wallet
  console.log('\n2️⃣ ARCHIVOS DEL WALLET');
  const requiredFiles = [
    'cwallet.sso',
    'ewallet.p12',
    'sqlnet.ora',
    'tnsnames.ora'
  ];

  requiredFiles.forEach(file => {
    const filePath = path.join(walletPath, file);
    const exists = fs.existsSync(filePath);
    
    if (exists) {
      const stats = fs.statSync(filePath);
      console.log(`✅ ${file}`);
      console.log(`   Tamaño: ${stats.size} bytes`);
      console.log(`   Permisos: ${(stats.mode & parseInt('777', 8)).toString(8)}`);
    } else {
      console.log(`❌ ${file} - NO ENCONTRADO`);
    }
  });

  // 3. Leer y verificar sqlnet.ora
  console.log('\n3️⃣ CONTENIDO DE sqlnet.ora');
  const sqlnetPath = path.join(walletPath, 'sqlnet.ora');
  if (fs.existsSync(sqlnetPath)) {
    const content = fs.readFileSync(sqlnetPath, 'utf8');
    console.log(content);
    
    // Verificar si apunta a la ubicación correcta
    if (content.includes('DIRECTORY') && !content.includes(walletPath) && !content.includes('TNS_ADMIN')) {
      console.log('\n⚠️  PROBLEMA DETECTADO:');
      console.log('   sqlnet.ora no apunta a la ubicación correcta del wallet');
      console.log('   Debe contener:', walletPath);
    }
  }

  // 4. Verificar tnsnames.ora
  console.log('\n4️⃣ TNS NAMES');
  const tnsnamesPath = path.join(walletPath, 'tnsnames.ora');
  if (fs.existsSync(tnsnamesPath)) {
    const content = fs.readFileSync(tnsnamesPath, 'utf8');
    const matches = content.match(/(\w+)_high\s*=/g);
    if (matches) {
      console.log('✅ Servicios encontrados:');
      matches.forEach(match => {
        console.log(`   - ${match.replace(/\s*=/, '')}`);
      });
    }
  }
}

// 5. Verificar Oracle Instant Client
console.log('\n5️⃣ ORACLE INSTANT CLIENT');
console.log('Path configurado:', instantClientPath);
if (instantClientPath && fs.existsSync(instantClientPath)) {
  console.log('✅ Carpeta existe');
  
  const requiredDlls = ['oci.dll', 'oraociei23.dll'];
  requiredDlls.forEach(dll => {
    const dllPath = path.join(instantClientPath, dll);
    console.log(fs.existsSync(dllPath) ? `✅ ${dll}` : `❌ ${dll} - NO ENCONTRADO`);
  });
} else {
  console.log('❌ Carpeta NO existe');
}

// 6. Verificar variables de entorno
console.log('\n6️⃣ VARIABLES DE ENTORNO (.env)');
console.log('DB_USER:', process.env.DB_USER);
console.log('DB_CONNECT_STRING:', process.env.DB_CONNECT_STRING);
console.log('TNS_ADMIN:', process.env.TNS_ADMIN);
console.log('ORACLE_CLIENT_LIB:', process.env.ORACLE_CLIENT_LIB);

console.log('\n' + '='.repeat(60));
console.log('\n💡 RECOMENDACIONES:');
console.log('1. Asegúrate de que sqlnet.ora tenga la ruta correcta');
console.log('2. Usa barras normales (/) en las rutas, no barras invertidas (\\)');
console.log('3. Verifica que cwallet.sso tenga permisos de lectura');
console.log('4. Si persiste el error, elimina y vuelve a descargar el wallet');
console.log('='.repeat(60));