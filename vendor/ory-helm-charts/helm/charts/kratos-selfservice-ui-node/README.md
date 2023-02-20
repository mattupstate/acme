# kratos-selfservice-ui-node

![Version: 0.27.1](https://img.shields.io/badge/Version-0.27.1-informational?style=flat-square) ![Type: application](https://img.shields.io/badge/Type-application-informational?style=flat-square) ![AppVersion: v0.10.1](https://img.shields.io/badge/AppVersion-v0.10.1-informational?style=flat-square)

A Helm chart for ORY Kratos's example ui for Kubernetes

## Values

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| affinity | object | `{}` |  |
| baseUrl | string | `""` | The baseUrl |
| deployment | object | `{"annotations":{},"automountServiceAccountToken":false,"extraEnv":[],"extraVolumeMounts":[],"extraVolumes":[],"labels":{},"nodeSelector":{},"resources":{},"tolerations":[],"topologySpreadConstraints":[]}` | Deployment configuration |
| deployment.extraEnv | list | `[]` | Array of extra envs to be passed to the deployment. Kubernetes format is expected - name: FOO   value: BAR |
| deployment.extraVolumes | list | `[]` | If you want to mount external volume For example, mount a secret containing Certificate root CA to verify database TLS connection. |
| deployment.nodeSelector | object | `{}` | Node labels for pod assignment. |
| deployment.tolerations | list | `[]` | Configure node tolerations. |
| deployment.topologySpreadConstraints | list | `[]` | Configure pod topologySpreadConstraints. |
| fullnameOverride | string | `""` |  |
| image | object | `{"pullPolicy":"IfNotPresent","repository":"oryd/kratos-selfservice-ui-node","tag":"v0.10.1"}` | Deployment image settings |
| image.tag | string | `"v0.10.1"` | ORY KRATOS VERSION |
| imagePullSecrets | list | `[]` |  |
| ingress | object | `{"annotations":{},"className":"","enabled":false,"hosts":[{"host":"chart-example.local","paths":[{"path":"/","pathType":"ImplementationSpecific"}]}],"tls":[]}` | Ingress configration |
| jwksUrl | string | `"http://oathkeeper-api"` | The jwksUrl |
| kratosAdminUrl | string | `"http://kratos-admin"` | Set this to ORY Kratos's Admin URL |
| kratosBrowserUrl | string | `"http://kratos-browserui"` | Set this to ORY Kratos's public URL accessible from the outside world. |
| kratosPublicUrl | string | `"http://kratos-public"` | Set this to ORY Kratos's public URL |
| nameOverride | string | `""` |  |
| projectName | string | `"SecureApp"` |  |
| replicaCount | int | `1` | Number of replicas in deployment |
| securityContext | object | `{"allowPrivilegeEscalation":false,"capabilities":{"drop":["ALL"]},"privileged":false,"readOnlyRootFilesystem":true,"runAsNonRoot":true,"runAsUser":1000}` | Deployment level securityContext |
| service | object | `{"name":"http","port":80,"type":"ClusterIP"}` | Service configuration |
| service.name | string | `"http"` | The service port name. Useful to set a custom service port name if it must follow a scheme (e.g. Istio) |

----------------------------------------------
Autogenerated from chart metadata using [helm-docs v1.11.0](https://github.com/norwoodj/helm-docs/releases/v1.11.0)