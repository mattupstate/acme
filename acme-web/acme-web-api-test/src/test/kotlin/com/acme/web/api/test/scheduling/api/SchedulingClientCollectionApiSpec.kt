package com.acme.web.api.test.scheduling.api

import com.acme.web.api.test.AcmeWebTestProjectConfig.Companion.apiServer
import com.acme.web.api.test.firstLinkedItemHref
import com.acme.web.api.test.mockUser
import com.acme.web.api.test.shouldEqualJson
import com.acme.web.api.test.shouldMatchJsonSchema
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.core.spec.style.ShouldSpec
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class SchedulingClientCollectionApiSpec : ShouldSpec({
  context("Client resource collection") {
    should("create a new client resource with valid input") {
      apiServer.http.post("/scheduling/clients") {
        contentType(ContentType.Application.Json)
        headers {
          mockUser()
        }
        setBody(
          """
          {
            "name": {
              "family": "Evans",
              "given": "Debbie"
            },
            "gender": "UNKNOWN",
            "phoneNumbers": [
              "917-555-5555"
            ],
            "emailAddresses": [
              "hello@world.com"
            ]
          }
          """
        )
      }.also {
        it shouldHaveStatus HttpStatusCode.OK
        it shouldMatchJsonSchema """
          {
            "type": "object",
            "properties": {
              "_links": {
                "type": "object",
                "properties": {
                  "self": {
                    "type": "string"
                  },
                  "items": {
                    "type": "array",
                    "items": {
                      "type": "string"
                    }
                  }
                }
              }
            },
            "additionalProperties": false
          }
        """
      }.firstLinkedItemHref().also { newResourceHref ->
        apiServer.http.get(newResourceHref) {
          headers {
            mockUser()
          }
        }.also {
          it shouldHaveStatus HttpStatusCode.OK
          it shouldMatchJsonSchema """
            {
              "type": "object",
              "properties": {
                "_links": {
                  "type": "object",
                  "properties": {
                    "self": {
                      "type": "string"
                    },
                    "items": {
                      "type": "array",
                      "items": {
                        "type": "string"
                      }
                    }
                  }
                },
                "id": {
                  "type": "string"
                },
                "names": {
                  "type": "array",
                  "items": {
                    "type": "object",
                    "properties": {
                      "family": {
                        "type": "string"
                      },
                      "given": {
                        "type": "string"
                      },
                      "suffix": {
                        "type": "string"
                      },
                      "prefix": {
                        "type": "string"
                      },
                      "period": {
                        "type": "object",
                        "properties": {
                          "start": {
                            "type": "string"
                          },
                          "end": {
                            "type": "string"
                          }
                        }
                      }
                    }
                  }
                },
                "gender": {
                  "type": "string"
                },
                "contactPoints": {
                  "type": "array",
                  "items": {
                    "type": "object",
                    "properties": {
                      "system": {
                        "type": "string"
                      },
                      "value": {
                        "type": "string"
                      },
                      "verifiedAt": {
                        "type": "string"
                      }
                    }
                  }
                }
              },
              "additionalProperties": false
            }
          """
          it shouldEqualJson """
            {
              "_links": {
                "self": {
                  "href": "$newResourceHref"
                }
              },
              "id": "${newResourceHref.split("/").last()}",
              "names": [{
                "family": "Evans",
                "given": "Debbie",
                "suffix": "",
                "prefix": "",
                "period": {
                  "start": null,
                  "end": null
                }
              }],
              "gender": "UNKNOWN",
              "contactPoints": [
                {
                  "value": "917-555-5555",
                  "system": "Phone",
                  "verifiedAt": null
                },
                {
                  "value": "hello@world.com",
                  "system": "Email",
                  "verifiedAt": null
                }
              ]
            }
          """
        }
      }
    }
    should("return error messages with invalid input") {
      apiServer.http.request("/scheduling/clients") {
        method = HttpMethod.Post
        contentType(ContentType.Application.Json)
        headers {
          mockUser()
        }
        setBody(
          """
          {
            "name": {},
            "gender": "BOGUS",
            "phoneNumbers": [],
            "emailAddresses": []
          }
          """
        )
      }.also {
        it shouldHaveStatus HttpStatusCode.BadRequest
        it shouldMatchJsonSchema """
          {
            "type": "object",
            "properties": {
              "_links": {
                "type": "object",
                "properties": {
                  "about": {
                    "type": "object",
                    "properties": {
                      "href": {
                        "type": "string"
                      }
                    }
                  },
                  "help": {
                    "type": "object",
                    "properties": {
                      "href": {
                        "type": "string"
                      }
                    }
                  }
                }
              },
              "total": {
                "type": "number"
              },
              "logRef": {
                "type": "string"
              },
              "message": {
                "type": "string"
              },
              "path": {
                "type": "string"
              },
              "_embedded": {
                "type": "object",
                "properties": {
                  "errors": {
                    "type": "array",
                    "items": {
                      "${"$"}": "/"
                    }
                  }
                }
              }
            },
            "additionalProperties": false
          }
        """
        it shouldEqualJson """
          {
            "_links": {
              "about": {
                "href": "/scheduling/clients"
              },
              "help": {
                "href": "/docs/help.html#validation_error"
              }
            },
            "message": "Validation error",
            "total": 5,
            "_embedded": {
              "errors": [
                {
                  "message": "size must be between 1 and 100",
                  "path": "/emailAddresses"
                },
                {
                  "message": "must be one of [MALE, FEMALE, TRANSGENDER, NON_BINARY, OTHER, UNKNOWN]",
                  "path": "/gender"
                },
                {
                  "message": "must not be blank",
                  "path": "/name/family"
                },
                {
                  "message": "must not be blank",
                  "path": "/name/given"
                },
                {
                  "message": "size must be between 1 and 100",
                  "path": "/phoneNumbers"
                }
              ]
            }
          }
        """
      }
    }
  }
})
