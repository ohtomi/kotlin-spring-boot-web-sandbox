
default: bootRun

bootRun:
	gradle bootRun

genkeypair:
	keytool -genkeypair -alias tomcat \
	  -storetype PKCS12 -keyalg RSA -keysize 2048 \
	  -keystore keystore.p12 -storepass mypassword \
	  -validity 3650

.PHONY: bootRun
