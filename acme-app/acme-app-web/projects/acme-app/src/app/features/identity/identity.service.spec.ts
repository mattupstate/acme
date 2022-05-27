import { HttpClient } from "@angular/common/http";
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { TestBed } from "@angular/core/testing";
import { SelfServiceLoginFlow, Session } from "@ory/kratos-client";
import { IdentityServiceSignInError, IdentityServiceUnauthorizedError, IdentityServiceUnexpectedError, KratosIdentityService, SignInErrorCause } from "./identity.service";

describe('KratosIdentityService', () => {
  const baseUrl = 'https://api'
  const registrationReturnUrl = 'http://localhost'

  let httpClient: HttpClient;
  let httpTestingController: HttpTestingController;
  let service: KratosIdentityService

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule
      ]
    });

    httpClient = TestBed.inject(HttpClient);
    httpTestingController = TestBed.inject(HttpTestingController);
    service = new KratosIdentityService(httpClient, baseUrl, registrationReturnUrl)
  })

  describe('::signIn', () => {
    it('should return an Identity object from a successful sign in flow', () => {
      service.signIn('hello@world.com', 'password').subscribe(
        (result) => {
          expect(result).toEqual({
            id: 'user123',
            email: 'hello@world.com',
            name: {
              given: 'Hello',
              family: 'World',
              preferred: 'Hello World'
            }
          });
        },
        (error) => { fail(error) }
      );

      mockSignInFlowWithTraits('user123', {
        name: {
          given: 'Hello',
          family: 'World',
          preferred: 'Hello World'
        },
        email: 'hello@world.com',
      });
    });

    it('should throw an IdentityServiceSignInError from a 400 response', () => {
      service.signIn('hello@world.com', 'password').subscribe(
        () => {
          fail('should throw error but did not')
        },
        (error: IdentityServiceSignInError) => {
          expect(error.cause).toEqual(SignInErrorCause.INVALID_CREDENTIALS);
        }
      );

      mockSignInFlowWithInvalidCredentials()
    });
  });

  describe('::whoAmiI', () => {
    it('should return an Identity object from a 200 response', () => {
      const mockKratosSession: Session = {
        id: '123',
        identity: {
          id: '123',
          schema_id: '123',
          schema_url: '',
          traits: {
            name: {
              given: 'Hello',
              family: 'World',
              preferred: 'Hello World'
            },
            email: 'hello@world.com',
          }
        }
      }

      service.whoAmI().subscribe((result) => {
        expect(result).toEqual({
          id: '123',
          email: 'hello@world.com',
          name: {
            given: 'Hello',
            family: 'World',
            preferred: 'Hello World'
          }
        });
      });

      const req = httpTestingController.expectOne(`${baseUrl}/sessions/whoami`);
      req.flush(mockKratosSession);

      httpTestingController.verify();
    });

    it('should throw an IdentityServiceUnauthorizedError from a 401 response', () => {
      service.whoAmI().subscribe(() => { }, (error) => {
        expect(error).toBeInstanceOf(IdentityServiceUnauthorizedError);
      });

      const req = httpTestingController.expectOne(`${baseUrl}/sessions/whoami`);
      req.flush({}, { status: 401, statusText: 'Not found' })

      httpTestingController.verify();
    });

    it('should throw an IdentityServiceUnexpectedError from anything other than a 401 response', () => {
      service.whoAmI().subscribe(() => { }, (error) => {
        expect(error).toBeInstanceOf(IdentityServiceUnexpectedError);
      });

      const req = httpTestingController.expectOne(`${baseUrl}/sessions/whoami`);
      req.flush({}, { status: 0, statusText: 'Unexpected error' })

      httpTestingController.verify();
    });
  });

  function mockSignInFlowWithTraits(userId: string, traits: {
    name: {
      given: string,
      family: string,
      preferred: string
    },
    email: string,
  }) {
    mockSignInFlow(
      {
        id: 'uuid-123',
        issued_at: '',
        expires_at: '',
        request_url: '',
        type: '',
        ui: {
          action: `${baseUrl}/self-service/login/browser`,
          method: '',
          nodes: [{
            attributes: {
              name: 'csrf_token',
              value: 'a value',
              disabled: false,
              node_type: '',
              type: ''
            },
            group: '',
            messages: [],
            meta: {},
            type: ''
          }]
        }
      },
      {
        session: {
          id: '123',
          identity: {
            id: userId,
            schema_id: '123',
            schema_url: '',
            traits
          }
        }
      }
    )
  };

  function mockSignInFlow(flowGetResponse: SelfServiceLoginFlow, flowPostResponse: { session: Session }) {
    const req = httpTestingController.expectOne(`${baseUrl}/self-service/login/browser`);
    req.flush(flowGetResponse);

    const req2 = httpTestingController.expectOne(`${baseUrl}/self-service/login/browser`);
    req2.flush(flowPostResponse);

    httpTestingController.verify();
  }

  function mockSignInFlowWithInvalidCredentials() {
    mockSignInFlowWithErrorCode(4000006);
  }

  function mockSignInFlowWithInactiveAccount() {
    mockSignInFlowWithErrorCode(4000010);
  }

  function mockSignInFlowWithErrorCode(code: number) {
    const mockKratosSignInFlow: SelfServiceLoginFlow = {
      id: 'uuid-123',
      issued_at: '',
      expires_at: '',
      request_url: '',
      type: '',
      ui: {
        action: `${baseUrl}/self-service/login/browser`,
        method: '',
        nodes: [{
          attributes: {
            name: 'csrf_token',
            value: 'a value',
            disabled: false,
            node_type: '',
            type: ''
          },
          group: '',
          messages: [],
          meta: {},
          type: ''
        }]
      }
    }

    const mockErrorResponse: SelfServiceLoginFlow = {
      ...mockKratosSignInFlow,
      ui: {
        action: `${baseUrl}/self-service/login/browser`,
        method: '',
        nodes: [],
        messages: [{
          id: code,
          text: '',
          type: ''
        }]
      }
    }

    const req = httpTestingController.expectOne(`${baseUrl}/self-service/login/browser`);
    req.flush(mockKratosSignInFlow);

    const req2 = httpTestingController.expectOne(`${baseUrl}/self-service/login/browser`);
    req2.flush(mockErrorResponse, { status: 400, statusText: 'Bad request' });

    httpTestingController.verify();
  }
});