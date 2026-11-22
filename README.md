# BountyTracker - Minecraft Plugin

## Projekt összefoglaló
A **BountyTracker** egy Minecraft plugin, ami Paper 1.21.1+ platformra alapszik. Ideális lehet PvP, de akár SMP szerverekre is. A plugin lehetővé teszi a játékosoknak, hogy fejpénzt tűzzenek ki egymásra különböző ércekkel.

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
- Másold a `target/bountytracker-1.0.2.jar` fájlt a Minecraft szervered `plugins/` mappájába.
- Indítsd el a szervert a következő paranccsal:
```bash
  java -Xms2G -Xmx4G -jar server.jar nogui
```
- A szerver induláskor automatikusan létrehozza a `plugins/BountyTracker/bounties.json` fájlt az adatok tárolásához.

## Használat és Parancsok
- `/bounty add <játékos> <érc> <összeg>`: Fejpénz kitűzése a megadott játékosra.
- `/bounty list`: Az aktív körözések kilistázása.
- **Fejpénz begyűjtése:** Ha egy játékos megöli a körözött célpontot, automatikusan megkapja a levont adók után járó érceket az inventoryjába.

## Megvalósított OOP Követelmények & Architektúra
- **Inkapszuláció:** Privát adattagok, validált konstruktorok és kontrollált hozzáférés a domain osztályokban (`Bounty`, `Currency`).
- **Absztrakció:** `TaxCalculationStrategy` interfész, valamint `Bounty` és `Currency` absztrakt ősosztályok a domain szerződések leírására.
- **Öröklődés és Polimorfizmus:** A `PlayerBounty` kiterjeszti a `Bounty` osztályt és felülírja a `getBountyType()` metódust. A fizetőeszközök (`DiamondCurrency`, `EmeraldCurrency`, `GoldCurrency`, `IronCurrency`) a `Currency` ősosztályból öröklődnek. A `getPayoutAmount()` futásidőben hívja meg a dinamikusan beállított `TaxCalculationStrategy` stratégiát.
- **Tervezési Minták (Design Patterns):**
    - *Strategy Pattern:* Adószámítási stratégiák (`StandardTaxStrategy` 10% adóval, `VipTaxStrategy` 5% adóval).
    - *Observer Pattern:* Eseményvezérelt működés a `BountyListener` osztályban (`PlayerDeathEvent`).
    - *Static Factory Method:* Érc példányosítása szöveges bemenetből (`Currency.fromInput(...)`).
    - *Template Method:* Plugin életciklus (`JavaPlugin`) és fejpénz értékelési váz (`Bounty`).
- **Generikusok és Kollekciók:** `List<PlayerBounty>` használata, rendezés `Comparable<Bounty>` segítségével az ércek értékszorzója (`valueMultiplier`) alapján.
- **Kivételkezelés:** Egyedi domain kivétel (`InvalidBountyException`) érvénytelen műveletek (pl. önfejpénz, duplikált kitűzés) kezelésére.
- **Adatperzisztencia:** JSON alapú fájlkezelés `BountyDataStorage` osztályban `try-with-resources` szerkezettel.