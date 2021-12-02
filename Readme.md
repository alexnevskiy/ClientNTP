# NTP Client на UDP

В соответствии с [RFC 5905](https://datatracker.ietf.org/doc/html/rfc5905) написан NTP клиент на *Java*, который отправляет NTP-сообщение серверу, ожидает ответа и выводит в консоль полученное сообщение, а также смещение по времени и задержку приёма-передачи.

## Реализация клиента

Для реализации модели NTP-сообщения написан класс [NTPMessage](https://github.com/alexnevskiy/ClientNTP/blob/master/src/main/java/model/NTPMessage.java), в котором описаны все используемые в протоколе поля.

При запуске клиента создаётся NTP-сообщение со всеми нулевыми полями кроме:

- `version` = 4
- `mode` = 3
- `transmitTimestamp` = текущее время в секундах + начальная эпоха (1970 - 1900)

Далее созданная дэйтаграмма отправляется на сервер, и клиент ожидает от него ответного сообщения. После получения пакета фиксируется время прибытия, и высчитывается задержка приёма-передачи, ![\delta](https://render.githubusercontent.com/render/math?math=%5Cdisplaystyle+%5Cdelta%0A) и смещение по времени ![\theta](https://render.githubusercontent.com/render/math?math=%5Cdisplaystyle+%5Ctheta%0A) по следующим формулам:

![\begin{gather*}
\delta = (t_3 - t_0) - (t_2 - t_1) \\
\theta = \frac{(t_1 - t_0) - (t_2 - t_3)}{2}, \text{где} \\
t_0 - \text{originate timestamp,} \\
t_1 - \text{receive timestamp,} \\
t_2 - \text{transmit timestamp,} \\
t_3 - \text{destination timestamp} \\
\end{gather*}
](https://render.githubusercontent.com/render/math?math=%5Cdisplaystyle+%5Cbegin%7Bgather%2A%7D%0A%5Cdelta+%3D+%28t_3+-+t_0%29+-+%28t_2+-+t_1%29+%5C%5C%0A%5Ctheta+%3D+%5Cfrac%7B%28t_1+-+t_0%29+-+%28t_2+-+t_3%29%7D%7B2%7D%2C+%5Ctext%7B%D0%B3%D0%B4%D0%B5%7D+%5C%5C%0At_0+-+%5Ctext%7Boriginate+timestamp%2C%7D+%5C%5C%0At_1+-+%5Ctext%7Breceive+timestamp%2C%7D+%5C%5C%0At_2+-+%5Ctext%7Btransmit+timestamp%2C%7D+%5C%5C%0At_3+-+%5Ctext%7Bdestination+timestamp%7D+%5C%5C%0A%5Cend%7Bgather%2A%7D%0A)

В конце выводится информация о полученном сообщении и вычисленные значения.

## Запуск клиента

Запуск клиента возможен как с аргументами, так и без. Возможные запуски клиента:

- Без аргументов - отправляет сообщение на стандартный NTP-сервер по адресу pool.ntp.org и порту 123
- `-a address port` - отправляет сообщение на NTP-сервер по указанному адресу и порту
- `-help` - выводит возможные аргументы для запуска клиента

## Примеры работы

### Без аргументов

```
NTP request sent, waiting for response...

NTP server: pool.ntp.org
=================================================
Leap indicator: 0
Version: 4
Mode: 4
Stratum: 2
Poll: 0
Precision: -25 (3E-8 seconds)
Root delay: 0,92 ms
Root dispersion: 1,22 ms
Reference id: 194.190.168.1
Reference timestamp:    02-12-2021 11:50:23.00832
Originate timestamp:    02-12-2021 11:51:09.00970
Receive timestamp:      02-12-2021 11:51:12.00419
Transmit timestamp:     02-12-2021 11:51:12.00419
=================================================
Destination timestamp:  02-12-2021 11:51:09.00972
Round-trip delay: 1,97 ms
Local clock offset: 2448,76 ms
```

### -a address port

```
-a time.google.com 123
NTP request sent, waiting for response...

NTP server: time.google.com
=================================================
Leap indicator: 0
Version: 4
Mode: 4
Stratum: 1
Poll: 0
Precision: -20 (9,5E-7 seconds)
Root delay: 0,00 ms
Root dispersion: 0,09 ms
Reference id: GOOG
Reference timestamp:    02-12-2021 11:57:18.00345
Originate timestamp:    02-12-2021 11:57:15.00883
Receive timestamp:      02-12-2021 11:57:18.00345
Transmit timestamp:     02-12-2021 11:57:18.00345
=================================================
Destination timestamp:  02-12-2021 11:57:15.00916
Round-trip delay: 34,00 ms
Local clock offset: 2445,37 ms
```

### -help

```
-help
No arguments - Start the client, which will send a request to pool.ntp.org
-a address port - Server address and port where the NTP message will be sent
```
