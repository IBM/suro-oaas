Package.describe({
  name: 'ilfrich:accounts-suro',
  version: '1.0.0',
  summary: 'Accounts login handler for SURO, using the REST endpoint to authenticate against the API and then stores an auth token which is used for API communication.',
  git: '',
  documentation: 'README.md'
});


Package.onUse(function(api) {
    api.versionsFrom('1.2.1');

    api.use(['templating', 'ecmascript'], 'client');

    api.use(['accounts-base', 'accounts-password', 'ecmascript', 'check'], 'server');

    api.addFiles(['suro_auth_client.js'], 'client');
    api.addFiles(['suro_auth_server.js'], 'server');

    api.export('SUROAUTH', 'server');
    api.export('SURO_DEFAULTS', 'server');
});