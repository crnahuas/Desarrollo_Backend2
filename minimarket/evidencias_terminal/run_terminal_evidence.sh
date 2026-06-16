#!/bin/zsh
cd /Users/cristian/Desktop/BackEnd2_Semana4/minimarket || exit 1
clear

echo "EVIDENCIA TERMINAL - JACOCO Y PRUEBAS"
echo "Proyecto: MiniMarket Plus"
echo
echo '$ mvn clean verify | egrep "jacoco:|Running com.minimarket|Tests run:|BUILD SUCCESS|All coverage checks|Total time|Finished at"'
echo

set -o pipefail
mvn clean verify 2>&1 | egrep "jacoco:|Running com.minimarket|Tests run:|BUILD SUCCESS|All coverage checks|Total time|Finished at"

echo
echo "$ awk -F, 'NR==1 || \$3==\"UsuarioServiceImpl\" || \$3==\"VentaServiceImpl\" {print}' target/site/jacoco/jacoco.csv"
awk -F, 'NR==1 || $3=="UsuarioServiceImpl" || $3=="VentaServiceImpl" {print}' target/site/jacoco/jacoco.csv

echo
echo "Captura lista para el informe."
echo "Presiona Enter para cerrar esta ventana."
read
