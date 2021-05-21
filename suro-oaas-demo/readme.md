# SURO Demo Application

> This MeteorJS application is a demo application connecting to the SURO (Surgical Unit Resource Optimization) service.
 
Other SURO components:

* [suro-oaas](https://github.ibm.com/aur/suro-oaas) - the Java component providing the data model, core logic and APIs
* [suro-oaas-admin](https://github.ibm.com/aur/suro-oaas-admin) - the MeteorJS service-level management application
 
## Running this application

To run this application, you need to:
 
* [Install MeteorJS](https://meteor.com/install)
* Have a SURO API server available (WLP / [suro-oaas-api](../suro-oaas-api).

By default the application will use http://localhost:9080 for the API endpoints (e.g. http://localhost:9080/api/hospitals)
To override this behaviour start the application with a `SERVICE_URL` environment variable pointing to the endpoint:

```
SERVICE_URL=http://localhost:9081/suro meteor --port 3000
```

Which would result in an endpoint call to http://localhost:9081/suro/api/hospitals.

In order to see the debug output from the API calls on the server console, you can start the application with the `DEBUG` 
environment variable:

```
DEBUG=1 meteor
```

## HTTPS / NginX

The following `/etc/nginx/nginx.conf` is recommended for running SURO on a virtual machine. Please adjust the path to 
the certificate and check [this NginX SSL Guide](https://github.ibm.com/aur/devops/blob/master/nginx-ssl.md).

**HTTP Endpoints**
- http://server-name - The Demo application (redirect to HTTPS)
- http://server-name:81 - The Admin application (redirect to HTTPS)
- http://server-name:84/_utils/index.html - CouchDB access

**HTTPS Endpoints**
- https://server-name/ - The Demo application
- https://server-name:444 - The Admin application
- https://server-name:484/_utils/index.html - CouchDB access

**Assumptions**
- the server name is `suro-oaas.sl.cloud9.ibm.com`
- you store a `server.key` and `suro-oaas.sl.cloud9.ibm.com.pem` for the key and certificate in `/etc/ssl`
- Demo application runs on port 3000
- Admin application runs on port 4000
- CouchDB runs on port 5984

```
user nginx;
worker_processes auto;
error_log /var/log/nginx/error.log;
pid /run/nginx.pid;

# Load dynamic modules. See /usr/share/nginx/README.dynamic.
include /usr/share/nginx/modules/*.conf;

events {
    worker_connections 1024;
}

http {
    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for"';

    access_log  /var/log/nginx/access.log  main;

    sendfile            on;
    tcp_nopush          on;
    tcp_nodelay         on;
    keepalive_timeout   65;
    types_hash_max_size 2048;

    include             /etc/nginx/mime.types;
    default_type        application/octet-stream;

    # Load modular configuration files from the /etc/nginx/conf.d directory.
    # See http://nginx.org/en/docs/ngx_core_module.html#include
    # for more information.
    include /etc/nginx/conf.d/*.conf;
    
    
    server {
        listen       80 default_server;
        listen       [::]:80 default_server;
        server_name  _;
        return 301 https://suro-oaas.sl.cloud9.ibm.com$request_uri;
    }

    server {
        listen       81 default_server;
        listen       [::]:81 default_server;
        server_name  _;
        return 301 https://suro-oaas.sl.cloud9.ibm.com:444$request_uri;
    }

    server {
        listen       84 default_server;
        listen       [::]:84 default_server;
        server_name  _;
        root         /usr/share/nginx/html;

        # Load configuration files for the default server block.
        include /etc/nginx/default.d/*.conf;

        location / {
            rewrite_log on;
            rewrite ^/(.*)$ /$1 break;
            proxy_pass http://127.0.0.1:5984;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection "upgrade";
        }

        error_page 404 /404.html;
            location = /40x.html {
        }

        error_page 500 502 503 504 /50x.html;
            location = /50x.html {
        }
    }
    
    # HTTPS endpoints
    
    server {
        ssl_protocols TLSv1.2;
        listen       443 ssl;
    	listen       [::]:443 ssl;
    	# replace with your server name
    	server_name suro-oaas.sl.cloud9.ibm.com;
    	root         /usr/share/nginx/html;
    
    	ssl on;
    	# provide the location of your server.key and pem file.
    	ssl_certificate /etc/ssl/suro-oaas.sl.cloud9.ibm.com.pem;
    	ssl_certificate_key /etc/ssl/server.key;
    
    	include /etc/nginx/default.d/*.conf;
    
    	# details see below
    	location / {
    	   rewrite_log on;
    	   # rewrite for URLs - details see below
    	   rewrite ^/(.*)$ /$1 break;
    	   # http port of the target service - details see below
    	   proxy_pass http://127.0.0.1:3000;
    	   proxy_set_header Upgrade $http_upgrade;
    	   proxy_set_header Connection "upgrade";
    	}
    }
    
    server {
        ssl_protocols TLSv1.2;
        listen       444 ssl;
    	listen       [::]:444 ssl;
    	# replace with your server name
    	server_name suro-oaas.sl.cloud9.ibm.com;
    	root         /usr/share/nginx/html;
    
    	ssl on;
    	# provide the location of your server.key and pem file.
    	ssl_certificate /etc/ssl/suro-oaas.sl.cloud9.ibm.com.pem;
    	ssl_certificate_key /etc/ssl/server.key;
    
    	include /etc/nginx/default.d/*.conf;
    
    	# details see below
    	location / {
    	   rewrite_log on;
    	   # rewrite for URLs - details see below
    	   rewrite ^/(.*)$ /$1 break;
    	   # http port of the target service - details see below
    	   proxy_pass http://127.0.0.1:4000;
    	   proxy_set_header Upgrade $http_upgrade;
    	   proxy_set_header Connection "upgrade";
    	}
    }

    server {
        ssl_protocols TLSv1.2;
        listen       484 ssl;
    	listen       [::]:484 ssl;
    	# replace with your server name
    	server_name suro-oaas.sl.cloud9.ibm.com;
    	root         /usr/share/nginx/html;
    
    	ssl on;
    	# provide the location of your server.key and pem file.
    	ssl_certificate /etc/ssl/suro-oaas.sl.cloud9.ibm.com.pem;
    	ssl_certificate_key /etc/ssl/server.key;
    
    	include /etc/nginx/default.d/*.conf;
    
    	# details see below
    	location / {
    	   rewrite_log on;
    	   # rewrite for URLs - details see below
    	   rewrite ^/(.*)$ /$1 break;
    	   # http port of the target service - details see below
    	   proxy_pass http://127.0.0.1:5984;
    	   proxy_set_header Upgrade $http_upgrade;
    	   proxy_set_header Connection "upgrade";
    	}
    }
}
``` 