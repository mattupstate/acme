import * as acme from "acme-web-client";
import AcmeApi = acme.com.acme.web.client.AcmeApi

const client = new AcmeApi("https://api-127-0-0-1.nip.io/scheduling");
client.get()