
default: bootRun

bootRun:
	gradle bootRun

genkeypair:
	keytool -genkeypair -alias tomcat -validity 3650 -storetype PKCS12 -keyalg RSA -keysize 2048 \
	  -keystore keystore.p12 -storepass mypassword

gomokuRun:
	@cd tool ; \
	gomoku run -tls -cert ./server.crt -key ./server.key

certificate:
	@cd tool ; \
	@rm ./server.key ./server.crt ; \
	openssl genrsa -out ./server.key 2048 ; \
	openssl req -new -x509 -sha256 -days 3650 -key ./server.key -out ./server.crt ; \
	sudo security add-trusted-cert -d -r trustRoot -k /Library/Keychains/System.keychain ./server.crt

importcert:
	@cd tool ; \
	keytool -importcert -alias gomoku -storepass changeit -cacerts -trustcacerts -file ./server.crt

deletecert:
	keytool -delete -alias gomoku -storepass changeit -cacerts

listcert:
	keytool -list -alias gomoku -storepass changeit -cacerts

.PHONY: bootRun genkeypair gomokuRun certificate importcert deletecert listcert
