# BountyTracker - Minecraft Plugin

## Projekt összefoglaló
A **BountyTracker** egy Minecraft plugin, ami Paper 1.21.1+ platformra alapszik. Ideális lehet PvP, de akár SMP szerverekre is. A plugin lehetővé teszi a játékosoknak, hogy fejpénzt tűzzenek ki egymásra, aminek eszköze a gyémánt.

##  Előfeltételek
- **Java Development Kit (JDK):** 21-es verzió (LTS)
- **Build eszköz:** Apache Maven 3.8+
- **Minecraft Szerver .jar:** Paper 1.21.1+

## Build & Run

### 1. Projekt fordítása és tesztelése Maven-nel
A projekt gyökérkönyvtárában futtasd az alábbi parancsokat:

```bash
# Clean build és JUnit tesztek futtatása:
mvn clean test

# Komplett kód fordítása és JAR fájl generálása:
mvn clean package
```
### 2. Plugin telepítése és futtatása szerveren
- Másold a `target/bountytracker-1.0.0.jar` fájlt a Minecraft szervered `plugins/` mappájába.
- Indítsd el a szervert a következő paranccsal:
```bash
  java -Xms2G -Xmx4G -jar server.jar nogui
```
- A szerver induláskor automatikusan létrehozza a `plugins/BountyTracker/bounties.json` fájlt az adatok tárolásához.

## Használat és Parancsok
- `/bounty add <játékos> <gyémánt_összeg>`: Fejpénz kitűzése a megadott játékosra (8 és 64 gyémánt között).
- `/bounty list`: Az aktív körözések kilistázása.
- **Fejpénz begyűjtése:** Ha egy játékos megöli a körözött célpontot, automatikusan megkapja a levont adók után járó gyémántokat az inventoryjába.

## Megvalósított OOP Követelmények & Architektúra
- **Inkapszuláció:** Privát adattagok, validált konstruktorok és kontrollált hozzáférés a modell osztályokban (`AbstractBounty`).
- **Absztrakció:** `TaxCalculationStrategy` interfész és `AbstractBounty` absztrakt osztály a domain szerződések leírására.
- **Öröklődés és Polimorfizmus:** A `PlayerBounty` felülírja a `getBountyType()` metódust. A `getPayoutAmount()` futásidőben hívja meg a dinamikusan beállított `TaxCalculationStrategy` stratégiát.
- **Tervezési Minta (Strategy Pattern):** Adószámítási stratégiák (`StandardTaxStrategy` 10% adóval, `VipTaxStrategy` 5% adóval).
- **Generikusok és Kollekciók:** `List<PlayerBounty>` használata, rendezés `Comparable<AbstractBounty>` segítségével.
- **Kivételkezelés:** Egyedi domain kivétel (`InvalidBountyException`) érvénytelen műveletek (pl. önfejpénz, duplikált kitűzés) kezelésére.
- **Adatperzisztencia:** JSON alapú fájlkezelés `BountyDataStorage` osztályban `try-with-resources` szerkezettel.