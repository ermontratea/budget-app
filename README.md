# Personal Budget Application - Backend REST API

Aplikacja backendowa służąca do zarządzania budżetem osobistym, napisana w technologii Spring Boot 4.0.6 oraz Java 21. Aplikacja pozwala na zarządzanie kontami bankowymi, rejestrację przychodów i wydatków, monitorowanie limitów budżetowych dla poszczególnych kategorii oraz generowanie raportów finansowych.

## Główne Funkcjonalności (Key Features)
- **Zarządzanie Kontami (Account Module):** Pełny zestaw operacji CRUD. System posiada blokadę biznesową uniemożliwiającą usunięcie konta, jeśli przypisane są do niego jakiekolwiek transakcje (zabezpieczenie spójności danych).
- **Zarządzanie Transakcjami (Transaction Module):** Automatyczne, transakcyjne przeliczanie salda konta w czasie rzeczywistym przy dodawaniu oraz usuwaniu transakcji (zarówno dla przychodów, jak i wydatków).
- **Dynamiczne Filtrowanie:** Możliwość filtrowania transakcji po opcjonalnych parametrach: zakres dat (?from=, ?to=) oraz kategoria (?category=), odporne na wielkość liter (Case-Insensitive w PostgreSQL).
- **Kontrola Limitów Budżetowych:** System automatycznie weryfikuje sumaryczne wydatki w danej kategorii. W przypadku przekroczenia zdefiniowanego limitu (np. Jedzenie - 500 zł), w odpowiedzi JSON generowane jest czytelne ostrzeżenie biznesowe.
- **Moduł Statystyk (Summary Module):** Generowanie podsumowań finansowych (łączne przychody, wydatki oraz wydatki pogrupowane po kategoriach).
- **Eksport do CSV:** Możliwość pobrania pełnej historii transakcji wybranego konta do pliku w formacie CSV.

## Wymagania Środowiskowe
- Java 21
- H2 (lokalnie projekt był rozwijany na PostgreSQL, zmiana pod koniec a H2)
- Gradle

## Jak uruchomić aplikację?

1. Sklonuj repozytorium na swój dysk.
2. W głównym katalogu projektu wykonaj polecenie:
   ./gradlew bootRun
   (W systemie Windows użyj komendy: gradlew bootRun)

## Dane testowe na start
Aplikacja posiada wbudowany moduł automatycznej inicjalizacji danych (DatabaseInitializer). Przy pierwszym uruchomieniu pusta baza zostanie automatycznie zasilona przykładowymi kontami i transakcjami, co umożliwia natychmiastowe testowanie bez konieczności ręcznego wprowadzania danych.

## Interaktywne GUI & Dokumentacja API
Po uruchomieniu aplikacji pełna, interaktywna dokumentacja techniczna wraz z narzędziem do testów manualnych (Swagger UI) jest dostępna pod adresem:
http://localhost:8080/swagger-ui/index.html

- ## Testy
Logika biznesowa została w pełni przetestowana za pomocą testów jednostkowych (JUnit 5 + Mockito) z zachowaniem czytelnej struktury Given-When-Then (BDD). Aby uruchomić testy, wpisz:
./gradlew test