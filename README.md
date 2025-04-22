# AWS CloudFront Cache - Spring Boot + AWS S3 + AWS CloudFront

## Wstęp
Aplikacja jest przykładem typu PoC (Proof of Concept), którego celem jest zademonstrowanie, jak bezpiecznie udostępniać prywatne pliki z AWS S3 poprzez CloudFront, z użyciem dwóch mechanizmów zabezpieczeń:
- Signed URL – podpisany adres URL z ograniczonym czasem ważności,
- Signed Cookies – podpisane ciasteczka, umożliwiające dostęp do zasobów przez określony czas.

Rozwiązanie bazuje na frameworku Spring Boot i zostało zaprojektowane jako prosty serwer aplikacyjny, który generuje odpowiednie mechanizmy dostępowe oraz udostępnia prosty frontend umożliwiający przetestowanie działania CloudFront z ograniczonym dostępem.

## Instrukcja uruchomienia
1. Skonfiguruj plik `application.yml` z danymi AWS CloudFront (domena, nazwa pliku video, identyfikator klucza publicznego z CloudFront) oraz kluczem PKCS12 (połączenie HTTPS jest wymagane dla Signed Cookies).
2. W pliku `hosts` (katalog C:\Windows\System32\drivers\etc\hosts dla Windows) dodaj wpis: `127.0.0.1 vid.xxxxxxxxxx.cloudfront.net`, gdzie `xxxxxxxxxx` to fragment naszej domeny z CloudFront.
3. Umieść plik `video.mp4` w S3 Bucket i skonfiguruj CloudFront, aby serwował ten plik.
4. Upewnij się, że posiadasz prywatny klucz CloudFront oraz ID klucza publicznego zapisanego w CloudFront.
5. Uruchom aplikację poleceniem:
   ```sh
   ./gradlew bootRun
   ```
6. Otwórz w przeglądarce `https://localhost/video-signedurl` lub `https://vid.xxxxxxxxxx.cloudfront.net/video-signedcookies` (gdzie "xxxxxxxxxx" to fragment naszej domeny z CloudFront).

Aplikacja pobiera plik video z CloudFront za pomocą Signed URL/Signed Cookies i wyświetla go na stronie.

## Oczekiwane działanie
Aplikacja pobiera plik video z CloudFront za pomocą wybranego mechanizmu zabezpieczeń i osadza go w tagu ```<video>```.
Dostęp do pliku nie powinien być możliwy bezpośrednio przez poniższe linki:
- https://xxxxxxxxxx.s3.eu-central-1.amazonaws.com/video.mp4 (gdzie "xxxxxxxxxx" to nazwa naszego bucketa w AWS S3).
- https://xxxxxxxxxx.cloudfront.net/video.mp4 (gdzie "xxxxxxxxxx" to fragment naszej domeny z CloudFront).

Plik jest chroniony przez podpisane adresy URL lub podpisane ciasteczka, a dostęp mają wyłącznie użytkownicy, którzy otrzymają poprawny token (URL lub ciasteczka) z aplikacji.
