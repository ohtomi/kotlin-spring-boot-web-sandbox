
default: ;

gomokuRun: server.crt
	env DEBUG=1 gomoku run -tls -cert ./server.crt -key ./server.key

server.crt: server.key
	openssl req -new -x509 -sha256 -days 3650 -key ./server.key -out ./server.crt

server.key:
	openssl genrsa -out ./server.key 2048

add-trusted-cert:
	sudo security add-trusted-cert -d -r trustRoot -k /Library/Keychains/System.keychain ./server.crt

importcert:
	keytool -importcert -alias gomoku -storepass changeit -cacerts -trustcacerts -file ./server.crt

deletecert:
	keytool -delete -alias gomoku -storepass changeit -cacerts

listcert:
	keytool -list -alias gomoku -storepass changeit -cacerts

.PHONY: default gomokuRun add-trusted-cert importcert deletecert listcert
