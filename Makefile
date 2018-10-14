
default: bootRun

bootRun:
	gradle bootRun

genkeypair:
	keytool -genkeypair -alias tomcat \
	  -storetype PKCS12 -keyalg RSA -keysize 2048 \
	  -keystore keystore.p12 -storepass mypassword \
	  -validity 3650

gomokuRun:
	@cd tool ; \
	gomoku run -tls -cert ./server.crt -key ./server.key

certificate:
	@cd tool ; \
	@rm ./server.key ./server.crt ; \
	openssl genrsa -out ./server.key 2048 ; \
	openssl req -new -x509 -sha256 \
	  -key ./server.key -out ./server.crt -days 3650

importca:
	keytool -import -alias gomoku \
	  -trustcacerts -file ../gomoku/cert/server.crt \
	  -cacerts

deleteca:
	keytool -delete -alias gomoku -cacerts

.PHONY: bootRun genkeypair importca deleteca
