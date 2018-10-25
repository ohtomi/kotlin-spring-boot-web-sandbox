
JIB_IMAGE_NAME = $(notdir $(CURDIR))
JIB_IMAGE_TAG = $(shell grep "version = " build.gradle | sed -E "s/.*'(.+)'$$/\1/")

default: ;

gomokuRun:
	@cd tool ; \
	make gomokuRun

bootRun: keystore.p12
	gradle bootRun

jibRun:
	docker run --rm -d -p 8443:8443 -p 8080:8080 --name sandbox $(JIB_IMAGE_NAME):$(JIB_IMAGE_TAG)

jibStop:
	docker stop sandbox

jibDockerBuild: keystore.p12
	@mkdir -p ./src/main/jib
	@cp keystore.p12 ./src/main/jib/
	gradle jibDockerBuild
	@rm -fr ./src/main/jib/

keystore.p12:
	keytool -genkeypair -alias tomcat -validity 3650 -storetype PKCS12 -keyalg RSA -keysize 2048 \
	  -keystore keystore.p12 -storepass mypassword

.PHONY: default gomokuRun bootRun jibRun jibStop jibDockerBuild
