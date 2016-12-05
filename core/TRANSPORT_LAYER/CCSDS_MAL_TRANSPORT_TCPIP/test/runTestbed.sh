if [ $# -eq 0 ]
then
	echo "No argument supplied for profile! You must provide a profile to run, i.e. ESA_TCPIP or SADT"
	exit 1;
fi

echo "Running MAL Testbed using profile $1"

cd CCSDS_MO_TRANS/
mvn -P ESA clean install
cd ../CCSDS_TCPIP_TESTBEDS/MOIMS_TESTBED_POM/
mvn clean install
cd ../MOIMS_TESTBED_UTIL/
mvn -P ESA clean install
cd ../MOIMS_TESTBED_MAL/
mvn -P $1 clean install
