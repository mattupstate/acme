FROM node:16.15.0 as dev
WORKDIR /usr/src
COPY package* ./
RUN npm install
COPY .eslintrc.json angular.json tsconfig.json ./
COPY projects ./projects
CMD ["npx", "ng", "serve", "--host", "0.0.0.0", "--disable-host-check"]

FROM dev as build
RUN npx ng build

FROM nginx:1.17.9-alpine
RUN mkdir -p /usr/share/acme
COPY --chown=nginx:nginx etc/nginx/conf.d/default.conf.nginx /etc/nginx/conf.d/default.conf
COPY --chown=nginx:nginx --from=build /usr/src/dist /usr/share/acme
