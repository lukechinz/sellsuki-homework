# sellsuki-homework
Sellsuki Homework

Easy way to run this project
1. download and install 'IntelliJ IDEA' from link https://www.jetbrains.com/idea/download/#section=mac ( or another IDE that you like )
2. import and run project from 'how_to_install.zip'
3. if you would ike to change port for run server. you can change in application.properties file

in this case I run this application on my server you can call this api
- link: http://103.27.203.54:8080/api/cart
- method POST
- example request
{
	"products": [
		{
			"productId": "9780857844774",
			"amount": 3
		},
		{
			"productId": "9781788701495",
			"amount": 1
		}
	]
}


Collection for test api using postman can use link below
https://www.getpostman.com/collections/8b5b752e8e40a05158fb
