/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import express from 'express';
import { Provider } from 'oidc-provider';

const oidc = new Provider('http://localhost:3000', {
    clients: [{
        client_id: 'my-client',
        client_secret: 'my-client-secret',
        grant_types: ['authorization_code'],
        redirect_uris: ['https://localhost:4200/'],
        post_logout_redirect_uris: ['https://localhost:4200/'],
    }],
    formats: {
        AccessToken: 'opaque',
        RefreshToken: 'opaque',
        IdToken: 'opaque'
    },
    features: {
        introspection: {
            enabled: true
        },
        revocation: { enabled: true },
        userinfo: { enabled: true },
    },
    clientBasedCORS(ctx, origin, client) {
        const allowedOrigins = ['https://localhost:4200'];
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
app.listen(3000, () => {
    console.log('OIDC provider listening on port 3000');
});
