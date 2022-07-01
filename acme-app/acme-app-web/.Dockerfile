FROM node:16.15.0

WORKDIR /usr/src
COPY package* ./
RUN npm install
COPY .eslintrc.json angular.json tsconfig.json ./
COPY projects ./projects

CMD ["npx", "ng", "serve", "--host", "0.0.0.0", "--disable-host-check"]
