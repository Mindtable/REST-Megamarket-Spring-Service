# Описание #

В данном задании вам предлагается реализовать бэкенд для веб-сервиса сравнения цен, аналогичный сервису [Яндекс Товары](https://yandex.ru/products). Обычно взаимодействие с такими сервисами происходит следующим образом:
1. Представители магазинов загружают информацию о своих товарах и категориях. Также можно изменять и удалять информацию о ранее загруженных товарах и категориях.
2. Покупатели, пользуясь веб-приложением, могут искать предложения разных магазинов, сравнивать цены и следить за их динамикой и историей.

Ваша задача - разработать REST API сервис, который позволяет магазинам загружать и обновлять информацию о товарах, а пользователям - смотреть какие товары были обновлены за последние сутки, а также следить за динамикой цен товара или категории за указанный интервал времени.

# Технические требования #

Реализуйте сервис на Python или Java в зависимости от выбранного направления школы. Сервис должен удовлетворять следующим требованиям:
- реализует спецификацию API, описанную в файле <code>[openapi.yaml](https://github.com/Mindtable/REST-Megamarket-Spring-Service/blob/master/openapi.yaml)</code>, и корректно отвечает на запросы проверяющей системы
- некоторые обработчики из них являются необязательными, их реализация позволит вам набрать дополнительное количество баллов
- сервис должен быть развернут в контейнере на `0.0.0.0:80`
- сервис должен обеспечивать персистентность данных (должен сохранять состояние данных при перезапуске)
- сервис должен обладать возможностью автоматического перезапуска при рестарте контейнера, в котором работает ваш бэкенд (этого можно достичь настройкой контейнера)
- после запуска сервиса время ответа сервиса на все методы API не должно превышать 1 секунду
- время полного старта сервиса не должно превышать 1 минуту
- импорт и удаление данных не превосходит 1000 элементов в 1 минуту
- RPS (Request per second) получения  статистики, недавних изменений и информации об элементе суммарно не превосходит 100 запросов в секунду
