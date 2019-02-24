using System.Collections.Generic;
using System.Security.Claims;
using IdentityModel;
using IdentityServer4.Models;
using IdentityServer4.Test;

namespace Identity
{
    internal class Clients
    {
        public static IEnumerable<Client> Get()
        {
            return new List<Client>
            {
                new Client
                {
                    ClientId = "todolistClient",
                    ClientName = "TodoList Client Credentials Client Application",
                    AllowedGrantTypes = {
                        GrantType.ClientCredentials,
                        "authorization_code"
                    },
                    ClientSecrets = new List<Secret>
                    {
                        new Secret("superSecretPassword".Sha256())
                    },
                    AllowedScopes = new List<string> {"todolistAPI.read", "todolistAPI.write"},
                    RedirectUris = new List<string> {"http://localhost:8081/login/idsvr", "http://localhost:9080/todos",  "http://localhost:9080"},
                }
            };
        }
    }
    
    internal class Resources {
        public static IEnumerable<IdentityResource> GetIdentityResources() {
            return new List<IdentityResource> {
                new IdentityResources.OpenId(),
                new IdentityResources.Profile(),
                new IdentityResources.Email(),
                new IdentityResource {
                    Name = "role",
                    UserClaims = new List<string> {JwtClaimTypes.Role}
                }
            };
        }

        public static IEnumerable<ApiResource> GetApiResources() {
            return new List<ApiResource> {
                new ApiResource {
                    Name = "todolistAPI",
                    DisplayName = "TodoList API",
                    Description = "TodoList API Access",
                    UserClaims = new List<string> {"role"},
                    ApiSecrets = new List<Secret> {new Secret("scopeSecret".Sha256())},
                    Scopes = new List<Scope> {
                        new Scope("todolistAPI.read"),
                        new Scope("todolistAPI.write")
                    }
                }
            };
        }
    }
    
    internal class Users {
        public static List<TestUser> Get() {
            return new List<TestUser> {
                new TestUser {
                    SubjectId = "5BE86359-073C-434B-AD2D-A3932222DABE",
                    Username = "kevin",
                    Password = "password",
                    Claims = new List<Claim> {
                        new Claim(JwtClaimTypes.Email, "kevin@knowledgespike.com"),
                        new Claim(JwtClaimTypes.Role, "admin")
                    }
                }
            };
        }
    }
}