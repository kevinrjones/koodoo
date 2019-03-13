## To Build

1. dotnet msbuild /t:PublishAll /p:Configuration=Release
1. dotnet msbuild /t:PublishAll

## Certificates

1. For the root cert
    1. Generate a key and a certificate
        1. ```openssl req -x509 -config openssl-ca.conf -newkey rsa:4096 -sha256 -nodes -out cacert.pem -outform PEM```
        1. Add the data needed for the root cert
        ``` shell
        touch index.txt
        echo '01' > serial.txt
        ```
    1. Install the root cert
        1. Import into KeyChain
        1. Double click and check 'Always Trust'
    1. Can now create a certificate for the site

1. For the development certificate
    1. Create a CSR
        1. ```openssl req -config openssl.localhost.conf -newkey rsa:2048 -sha256 -nodes -out dev.knowledgespike.com.csr -keyout dev.knowledgespike.com.key -outform PEM```
    1. Create the cert from the csr
        1. ```openssl ca -config openssl-ca.conf -policy signing_policy -extensions signing_req -out dev.knowledgespike.com.pem -infiles dev.knowledgespike.com.csr```
    1. Import this cert into the KeyChain (not sure that this is necessary)
    1. Make sure that Nginx (or whichever server you are using) references this certificate (see config below)
    1. To convert the cert to 'pfx' use ```openssl pkcs12 -inkey dev.knowledgespike.com.key -in dev.knowledgespike.com.pem -export -out dev.knowledgespike.com.pfx -certfile cacert.pem```
        1. Use 'p4ssw0rd' as the password
        1. Copy this file to location where pfx is read from by Kestrel
        1. This is used by 'Kestrel' when running the app locally (without Nginx)
    1. Add it to the keychain and make sure that its CA is trusted

## Java

1. Install root certificate in Java KeyStore which depends on the version of Java installed (/Library/Java/JavaVirtualMachines/jdk1.8.0_192.jdk/Contents/Home/jre/lib/security/cacerts or /Library/Java/JavaVirtualMachines/openjdk-11.0.1.jdk/Contents/Home/lib/security/cacerts)
1. Download 'Keystore Explorer' application
1. `sudo /Applications/KeyStore\ Explorer.app/Contents/MacOS/KeyStore\ Explorer`
1. Default password is 'changeit'
1. Import trusted certificate (cacert.pem)

## For Charles

1. Add Charles SSL cert to Chrome ( In Charles: Help->SSL Proxying->Install Charles Root Certificate)
1. Add Charles SSL cert to Java VMs (same locations as above)