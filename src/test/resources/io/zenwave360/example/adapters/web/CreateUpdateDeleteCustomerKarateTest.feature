@openapi-file=src/main/resources/public/apis/openapi.yml
Feature: CreateUpdateDeleteCustomerKarateTest

Background:
* url baseUrl
# * def auth = { username: '', password: '' }
* def authHeader = call read('classpath:karate-auth.js') auth
* configure headers = authHeader || {}

@business-flow
@operationId=createCustomer,updateCustomer,deleteCustomer,getCustomer
Scenario: CreateUpdateDeleteCustomerKarateTest

# createCustomer
Given path '/customers'
And request
"""
{
  "id" : 21,
  "version" : 13,
  "name" : "name-5ugq20772n1",
  "email" : "rosaria.hyatt@gmail.com",
  "addresses" : [ {
    "street" : "street-z",
    "city" : "West Edmundbury"
  } ],
  "paymentMethods" : [ {
    "id" : 37,
    "version" : 10,
    "type" : "MASTERCARD",
    "cardNumber" : "cardNumber-9c2xzm77ig5"
  } ]
}
"""
When method post
Then status 201
* def createCustomerResponse = response
* def customerId = response.id
# TODO: Add response validation


# updateCustomer
* def pathParams = { id: 2 }
Given path '/customers/', pathParams.id
And request
"""
{
  "id" : 79,
  "version" : 7,
  "name" : "name-mu8j1eqcaoy2a7ex9uup1",
  "email" : "jerica.emmerich@hotmail.co",
  "addresses" : [ {
    "street" : "street-7ug0jz4c5ghumuz2yst",
    "city" : "Sydneychester"
  } ],
  "paymentMethods" : [ {
    "id" : 30,
    "version" : 82,
    "type" : "MASTERCARD",
    "cardNumber" : "cardNumber-f"
  } ]
}
"""
When method put
Then status 200
* def updateCustomerResponse = response
* def customerId = response.id
# TODO: Add response validation


# deleteCustomer
* def pathParams = { id: 42 }
Given path '/customers/', pathParams.id
When method delete
Then status 204
* def deleteCustomerResponse = response
# TODO: Add response validation


# getCustomer
* def pathParams = { id: 71 }
Given path '/customers/', pathParams.id
When method get
Then status 404
* def getCustomerResponse = response
* def customerId = response.id
# TODO: Add response validation


