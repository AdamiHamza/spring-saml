#!/bin/bash

#===============================
#:::::::::: Key Store ::::::::::
#===============================
KEYSTORE_FILE=samlKeystore.jks
KEYSTORE_PASSWORD=nalle123

#===============================
#:::::::::::: IDP'S ::::::::::::
#===============================
#----------SSO CIRCLE-----------
SSO_CIRCLE_IDP_HOST=idp.ssocircle.com
SSO_CIRCLE_IDP_PORT=443
SSO_CIRCLE_CERTIFICATE_FILE=ssocircle.cert

openssl s_client -host ${SSO_CIRCLE_IDP_HOST} -port ${SSO_CIRCLE_IDP_PORT} -prexit -showcerts </dev/null | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > ${SSO_CIRCLE_CERTIFICATE_FILE}
keytool -delete -alias ssocircle -keystore ${KEYSTORE_FILE} -storepass ${KEYSTORE_PASSWORD}
keytool -import -alias ssocircle -file ${SSO_CIRCLE_CERTIFICATE_FILE} -keystore ${KEYSTORE_FILE} -storepass ${KEYSTORE_PASSWORD} -noprompt
rm ${SSO_CIRCLE_CERTIFICATE_FILE}

#----------SSO CIRCLE-----------
