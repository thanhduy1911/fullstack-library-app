export const oktaConfig = {
    clientId: `0oaocig7zkxRXc0nR5d7`,
    issuer: `https://dev-58558560.okta.com/oauth2/default`,
    redirectUri: `https://localhost:3000/login/callback`,
    scopes: ['openid', 'profile', 'email'],
    pkce: true,
    disableHttpsCheck: true,
    useClassicEngine: true
}