local claims = {
  email_verified: true
} + std.extVar('claims');

{
  identity: {
    traits: {
      [if "email" in claims && claims.email_verified then "email" else null]: claims.email,
      name: {
        given: claims.given_name,
        family: claims.family_name,
        preferred: claims.given_name
      }
    },
  },
}
