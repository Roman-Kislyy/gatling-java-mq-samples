#!/bin/bash -ex

KEY=server.key
CERT=server.crt
PKCS=server.p12
PASSWORD=changeit

# Create private key
openssl req \
		-newkey rsa:2048 -nodes -keyout ${KEY} \
		-subj "/CN=localhost" \
		-addext "subjectAltName = DNS:localhost" \
		-x509 -days 36500 -out ${CERT}
		
# Add the key and cert to keystore
openssl pkcs12 \
		-inkey ${KEY} \
		-in ${CERT} \
		-export -out ${PKCS} \
		-password pass:${PASSWORD} \
		
# Add the certificate to a truststore
keytool -import \
		-alias server-cert \
		-file ${CERT} \
		-keystore client-trust.jks \
		-storepass ${PASSWORD} \
		-noprompt
		