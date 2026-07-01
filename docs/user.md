# USER API SPec

## Register User

Endpoint : POST /api/users

Request body:

````json
{
  "username": "indra",
  "password" : "rahasia",
  "name" : "Indra Dwi Prabowo"
}
````

Response body (Sukses):
````json
{
  "data" : "OK"
}
````

Response body (Gagal):
````json
{
  "errors" : "Username or password musk not blank, ???"
}
````

## Login User

Endpoint : POST /api/auth/login

Request body:

````json
{
  "username": "indra",
  "password" : "rahasia"
}
````

Response body (Sukses):
````json
{
  "token" : "TOKEN",
  "expiredAt" : 2342342423423 // milisecounds
}
````

Response body (failed, 401):
````json
{
  "errors" : "Username or password wrong"
}
````

## Get User

Endpoint : POST /api/users/curerent

Request Header:
- X-API-TOKEN : Token (Mandatory)

Response body (Sukses):
````json
{
  "data" : {
    "username" : "indra",
    "name" : "Indra Dwi Prabowo"
  }
}
````

Response body (Gagal):
````json
{
  "errors" : "Unauthorized"
}
````

## Update User

Endpoint : PACTH /api/users/curerent

Request Header:
- X-API-TOKEN : Token (Mandatory)

Requiest Body :
````json
{
  "name" : "Indra Dwi", // putif only want to update name
  "password" : "new password" // putif only want to update name
}
````

Response body (Sukses):
````json
{
  "data" : {
    "username" : "indra",
    "name" : "Indra Dwi Prabowo"
  }
}
````

Response body (Failed, 401):
````json
{
  "errors" : "Unauthorized"
}
````


## Logout User
Endpoint : DELETE /api/users/curerent

Request Header:
- X-API-TOKEN : Token (Mandatory)

Response body (Sukses):
````json
{
  "data" : "OK"
}
````
