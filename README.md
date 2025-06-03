ShareIt
Шаблон проекта для сервиса ShareIt.
Сервис позволяет пользователям делиться вещами.

    Профили запуска:
in-memory : Работа с хранилищем в памяти (без базы данных).
h2 : Использование встроенной базы данных H2 (для разработки и тестирования).
postgres : Подключение к PostgreSQL (для production).
  
    Как запускать:
Через командную строку
Для H2: 
-Dspring.profiles.active=h2
Для PostgreSQL: 
-Dspring.profiles.active=postgres 
Для хранилища в памяти: 
-Dspring.profiles.active=in-memory

Через Maven
С H2:
mvn spring-boot:run -Dspring.profiles.active=h2
С PostgreSQL:
mvn spring-boot:run -Dspring.profiles.active=postgres
С хранилищем в памяти:
mvn spring-boot:run -Dspring.profiles.active=in-memory