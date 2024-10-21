# Local OpenID Connect Server

## Configuration

In order to use the local oidc provider you need to configure your project with the values :
- client id: 'my-client'
- client secret: 'my-client-secret'


## Start OIDC-provider

Open a new terminal

Start installing the dependencies with : $ npm install

Start the local server with : $ npm start

The server will start on port 3000

## How to use

Start Chutney with the profile sso-auth to enable sso

On the login page you should see the SSO button below the login button.

This will redirect you on the OIDC-provider interface. You can write the credential you want as long as the username is known in the authorization.json file

After completing the OIDC-provider workflow you should be authenticated and redirected to home screen of Chutney

OIDC-provider will keep you connected until you restart the OIDC-provider server
