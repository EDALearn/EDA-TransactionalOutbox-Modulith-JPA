@openapi-file=src/main/resources/public/apis/openapi.yml
Feature: CustomerApi

Background:
* url baseUrl
# * def auth = { username: '', password: '' }
* def authHeader = call read('classpath:karate-auth.js') auth
* configure headers = authHeader || {}

@operationId=createCustomer
Scenario: createCustomer

Given path '/customers'
And request
"""
{
  "name" : "name-iv8wf61cpxe13fa3j0",
  "email" : "kraig.hermann@gmail.com",
  "addresses" : [ {
    "street" : "street-wivpwm357mj4r1",
    "city" : "Connellyside"
  } ],
  "paymentMethods" : [ {
    "type" : "VISA",
    "cardNumber" : "cardNumber-ydvoeke77t3z"
  } ]
}
"""
When method post
Then status 201
* def createCustomerResponse = response
* def customerId = response.id
# TODO: Add response validation


@operationId=getCustomer
Scenario: getCustomer

* def pathParams = { id: 53 }
Given path '/customers/', pathParams.id
When method get
Then status 200
* def getCustomerResponse = response
* def customerId = response.id
# TODO: Add response validation


@operationId=updateCustomer
Scenario: updateCustomer

* def pathParams = { id: 53 }
Given path '/customers/', pathParams.id
And request
"""
{
  "name" : "name-t58rqm1n6zb",
  "email" : "reta.wiza@gmail.com",
  "addresses" : [ {
    "street" : "street-e2pn9ih8t7du14h93",
    "city" : "East Genesis"
  } ],
  "paymentMethods" : [ {
    "type" : "MASTERCARD",
    "cardNumber" : "cardNumber-qx3ag249vxzjgkh"
  } ]
}
"""
When method put
Then status 200
* def updateCustomerResponse = response
* def customerId = response.id
# TODO: Add response validation


@operationId=deleteCustomer
Scenario: deleteCustomer

* def pathParams = { id: 89 }
Given path '/customers/', pathParams.id
When method delete
Then status 204
* def deleteCustomerResponse = response
# TODO: Add response validation


@operationId=searchCustomers
Scenario: searchCustomers

Given path '/customers/search'
And def queryParams = { page: 71, limit: 20, sort: ['name:asc'] }
And request
"""
{
  "name" : "name-xrx9m9s1",
  "email" : "tyron.heidenreich@yahoo.co",
  "city" : "Bellamouth",
  "state" : "state-y1x18r2h"
}
"""
When method post
Then status 201
* def searchCustomersResponse = response
* def customerPaginatedId = response.id
# TODO: Add response validation


