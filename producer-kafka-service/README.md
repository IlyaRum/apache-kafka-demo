# Запуск сервисов

!!! Apache Kafka в Docker должна быть запущена.

Запустите оба сервиса producer и consumer.

После этого вы сможете отправить сообщение через POST-запрос на /send-message endpoint Producer Service, 
а Consumer Service получит и обработает это сообщение.

Пример команды для отправки сообщения:

`curl -X POST http://localhost:8080/send-message?message="message`

На стороне Consumer Service в логах увидите сообщение:

`Received Message: message`