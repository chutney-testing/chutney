/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import express from 'express';
import { Provider } from 'oidc-provider';
import * as dotenv from 'dotenv';

dotenv.config()

const oidc = new Provider('http://localhost:3000', {
    clients: [{
        client_id: process.env.CLIENT_ID,
        client_secret: process.env.CLIENT_SECRET,
        grant_types: [process.env.GRANT_TYPE],
        redirect_uris: process.env.REDIRECT_URI.split(' '),
        post_logout_redirect_uris: process.env.REDIRECT_URI.split(' '),
    }],
    formats: {
        AccessToken: process.env.TOKEN_FORMAT,
        RefreshToken: process.env.TOKEN_FORMAT,
        IdToken: process.env.TOKEN_FORMAT
    },
    features: {
        introspection: {
            enabled: true
        },
        revocation: { enabled: true },
        userinfo: { enabled: true },
    },
    clientBasedCORS(ctx, origin, client) {
        const allowedOrigins = process.env.REDIRECT_URI.split(' ');
        return allowedOrigins.includes(origin);
    },
    async findAccount(ctx, id) {
        return {
            accountId: id,
            async claims(use, scope) { return { sub: id }; },
        };
    }
});

const app = express();
app.use(oidc.callback());
const port = parseInt(process.env.PORT, 10)
app.listen(port, () => {
    console.log(`OIDC provider listening on port ${port}`);
});
