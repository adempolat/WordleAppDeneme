package com.adempolat.wordleapp

import kotlin.random.Random

object WordListHelper {

    private val words = listOf(
        "ABİDE", "ABİYE", "ABONE", "ACABA", "ACEMİ", "ACIMA", "ADALI", "ADANA", "ADRES", "AFAKİ", "AFGAN", "AFİŞE",
        "AHLAK", "AKRAN", "AKSAM", "ALACA", "ALÇAK", "ALMAÇ", "ALMAK", "ANLAM", "ANTRE", "ARMUT", "ASABİ", "ASKER",
        "ATAMA", "AYGIR", "AYRAN", "BACAK", "BADEM", "BAHAR", "BAHÇE", "BAHÇE", "BAHRİ", "BALIK", "BALON", "BAMYA",
        "BANYO", "BARAJ", "BARUT", "BASİT", "BATAK", "BATIL", "BATUR", "BEŞİR", "BETON", "BİBER", "BİÇİM", "BİÇME",
        "BİDEN", "BİLET", "BİLİN", "BİRAZ", "BİTAP", "BİTKİ", "BİTME", "BİTİŞ", "BIÇAK", "BOYAR", "BOYUN", "BOYUT",
        "BUGÜN", "BUHAR", "BULUT", "CACIK", "CAMCI", "CAVUR", "CENİN", "CEVİZ", "CİMRİ", "CIVIK", "CÜMLE", "CÜSSE",
        "ÇAKAR", "ÇAMAŞ", "ÇANAK", "ÇANTA", "ÇARIK", "ÇARŞI", "ÇATAL", "ÇATAK", "ÇAYIR", "ÇEKİÇ", "ÇIKTI", "ÇİLEK",
        "ÇİZGİ", "ÇİNKO", "ÇİNKO", "ÇİZİM", "ÇÖPÇÜ", "ÇORAP", "ÇÖZÜM", "ÇUVAL", "ÇÜRÜK", "DAHİL", "DAİMA", "DAİMİ",
        "DALGA", "DARBE", "DAVET", "DAYAK", "DAYAN", "DEFİN", "DELİL", "DEMİR", "DENGE", "DENİZ", "DEREK", "DERİN",
        "DİLEK", "DİREN", "DİREK", "DİĞER", "DOĞAN", "DOĞRU", "DOLAP", "DOLGU", "DOLMA", "DOMUZ", "DORUK", "DÖNME",
        "DÖNÜŞ", "DÖVİZ", "DUYGU", "DUYUM", "DUMAN", "DURUM", "DÜNYA", "DÜŞME", "EBEDİ", "ECDAT", "EFSUN", "EĞRİ",
        "EKMEK", "EKSİK", "EMSAL", "ENFES", "ENDER", "ENKAZ", "ERDEM", "ERKEK", "ERZİN", "ESMER", "ESPRİ",
        "EŞARP", "EVLAT", "EVVEL", "FASİL", "FAKİR", "FELAK", "FENER", "FERAH", "FİKRİ", "FİYAT", "FİKİR", "FUNDA",
        "FULAR", "GAMLI", "GARİP", "GAZOZ", "GEYİK", "GİZLİ", "GİYİM", "GÖKÇE", "GÖREV", "GÖMÜT", "GÖNÜL",
        "GÖRÜŞ", "GÜMÜŞ", "GÜNAH", "GÜREŞ", "GÜZEL", "HAFTA", "HALKA", "HARAM", "HAVVA", "HELVA",  "HİNDİ",
        "HOŞAF", "HOROZ", "HURMA", "IBRAZ", "ILGAZ", "IRGAT", "İÇSEL", "İLHAM", "İNMEK",
        "İNKAR", "İNSAN", "İPUCU", "İPTAL", "İSHAL", "İSYAN", "İZMİR", "JARSE", "JAPON", "JOKEY", "KAĞIT",
        "KABAN", "KABİN", "KABUL", "KADIN", "KALEM", "KALIN", "KARIN", "KARŞI", "KASIM", "KAVUN", "KAZAK",
        "KEKİK", "KEMER", "KİBİR", "KİBAR", "KİLER", "KİRAZ", "KİRPİ", "KIZAK",
        "KOÇAK", "KOYUN", "KOYUN", "KREDİ", "KURAN", "KUMRU", "KUŞAK", "KUTLU", "KÜÇÜK",
        "KÜTÜK", "LAMBA", "LATİF", "LAVAŞ", "LAVTA", "LEHİM", "LEVHA", "LİDER", "LİSAN", "LÜZUM",
        "MADDE", "MAKAS", "MARUL", "MASAL", "MEKKE", "MELEK", "MELEZ", "MİRAS", "MISIR",
        "MISRA", "NABIZ", "NADİR", "NAMAZ", "NAMUS", "NARİN", "NALAN", "NASIL",
        "NAZİK", "NİĞDE", "NİNNİ", "NİSAN", "NİŞAN", "OĞLAK", "OMLET",
        "ÖDÜNÇ", "ÖLÇEK", "ÖNLÜK", "ÖVMEK", "PASAK", "PİLOT",
        "POLİS", "POŞET", "RADYO", "RAMPA", "REHİN", "REÇEL", "RİMEL", "ROMAN", "RÜKÜŞ",
        "SAHİL", "SAKİZ", "SALON", "SALÇA", "SAYIM", "SAVUNMA", "SAVUR", "SAVAŞ", "SAYGI",
        "SEHPA", "SELAM", "SERÇE", "SİNİR", "SİTEM", "SOĞAN", "ŞAHİN", "ŞARKI",
        "ŞAFAK", "ŞİFRE", "ŞURUP", "TAHTA", "TAKIM",  "TAHİN", "TAVUK","TEKNE", "TELAŞ",
        "TEMİZ", "TEPSİ", "TİRİT", "TİLKİ", "TİNER", "UZMAN",
        "UYGUN", "ÜÇGEN", "ÜSTEL", "ÜZGÜN", "VAKIF", "VİDEO",
        "VİŞNE", "YAKIN", "YALIN",
    )

    private val sixLetterWords = listOf(
        "MEŞHUR", "NEŞELİ", "YAPRAK",
        "ABAJUR", "ABAKÜS", "ABANIŞ", "ABANMA", "ABANOZ", "ABARTI", "ABASIZ", "ABAZAN", "ABDEST", "ABLACI", "ABLUKA", "ABORDA", "ABRAMA", "ABSENT", "ABSTRE", "ABSÜRT", "ABUKÇA", "ACAYİP",
        "ACEMCE", "ACENTE", "ACIGÖL", "ACIKIŞ", "ACIKLI", "ACIKMA", "ACILIK", "ACIMAK", "ACIMIK", "ACIMSI", "ACINIŞ", "ACINMA", "ACIRAK", "ACIRGA", "ACISIZ", "ACITIŞ", "ACITMA", "ACIYIŞ",
        "ACİLEN", "ACYOCU", "AÇACAK", "AÇELYA", "AÇIKÇA", "AÇIKÇI", "AÇILIM", "AÇILIŞ", "AÇILMA", "AÇINIM", "AÇINMA", "AÇISAL", "AÇKICI", "AÇKILI", "AÇMACI", "ADACIK", "ADACYO", "ADAKLI",
        "ADALAR", "ADALET", "ADAMAK", "ADAMCA", "ADANIŞ", "ADANMA", "ADAPTE", "ADATIŞ", "ADATMA", "ADAVET", "ADAYIŞ", "ADEMCİ", "ADENİT", "ADETÇE", "ADİLİK", "ADLİYE", "AFACAN", "AFAKAN",
        "AFERİN", "AFİŞÇİ", "AFİYET", "AFOROZ", "AFRİKA", "AGANTA", "AGNOSİ", "AGNOZİ", "AGRAFİ", "AGREGA", "AGUCUK", "AĞABEY", "AĞACIK", "AĞAÇLI", "AĞAÇSI", "AĞALIK", "AĞARIK", "AĞARIŞ",
        "AĞARMA", "AĞARTI", "AĞDACI", "AĞDALI", "AĞIMLI", "AĞINMA", "AĞIRCA", "AĞITÇI", "AĞIZLI", "AĞLAMA", "AĞLATI", "AĞNAMA", "AĞRILI", "AĞRIMA", "AHACIK", "AHARLI", "AHESTE", "AHIRLI",
        "AHİLİK", "AHİREN", "AHİRET", "AHLAKİ", "AHLAMA", "AHUVAH", "AİLECE", "AİLEVİ", "AJANDA", "AJURLU", "AKAĞAÇ", "AKAMET", "AKARCA", "AKARET", "AKARSU", "AKASMA", "AKASYA", "AKBABA",
        "AKÇELİ", "AKDARI", "AKIBET", "AKILCI", "AKILLI", "AKIMCI", "AKIMLI", "AKINCI", "AKINTI", "AKIŞLI", "AKIŞMA", "AKITAÇ", "AKITIŞ", "AKITMA", "AKİTLİ", "AKLAMA", "AKLİYE", "AKÖREN",
        "AKRABA", "AKSAMA", "AKSATA", "AKSEKİ", "AKSİNE", "AKSONA", "AKSUNA", "AKTÖRE", "AKTRİS", "AKTÜEL", "AKTÜER", "AKYAKA", "AKYAZI", "AKYURT", "ALABAŞ", "ALACAK", "ALAÇAM", "ALAÇIK",
        "ALADAĞ", "ALAKOK", "ALAMET", "ALANYA", "ALAPLI", "ALARGA", "ALARMA", "ALAŞIM", "ALATAV", "ALATEN", "ALAYCI", "ALAYİŞ", "ALAYLI", "ALAYSI", "ALBATR", "ALBENİ", "ALBİNO", "ALÇICI",
        "ALÇILI", "ALDANÇ", "ALEKSİ", "ALEMCİ", "ALENEN", "ALERJİ", "ALESTA", "ALETLİ", "ALEVLİ", "ALFABE", "ALGLER", "ALIKÇA", "ALIMCI", "ALIMLI", "ALINDI", "ALINIŞ", "ALINLI", "ALINMA",
        "ALINTI", "ALIŞIK", "ALIŞKI", "ALIŞMA", "ALİAĞA", "ALİDAT", "ALİVRE", "ALKALİ", "ALLAMA", "ALLAME", "ALPAKA", "ALPAKS", "ALPLIK", "ALŞİMİ", "ALTILI", "ALTLIK", "ALTMIŞ", "ALTSIZ",
        "ALTUNİ", "ALTÜST", "ALUCRA", "ALÜFTE", "ALÜMİN", "ALVEOL", "ALYANS", "AMAÇLI", "AMALIK", "AMANIN", "AMASRA", "AMASYA", "AMATÖR", "AMAZON", "AMBALE", "AMBLEM", "AMENNA", "AMENTÜ",
        "AMETAL", "AMFİBİ", "AMFORA", "AMİLAZ", "AMİPLİ", "AMİRAL", "AMİRCE", "AMİTOZ", "AMNEZİ", "AMORTİ", "AMYANT", "ANACIK", "ANACIL", "ANADUT", "ANAFOR", "ANALIK", "ANALİZ", "ANALOG",
        "ANAMAL", "ANAMUR", "ANANAS", "ANARŞİ", "ANASIL", "ANASIR", "ANASIZ", "ANASON", "ANBEAN", "ANÇÜEZ", "ANEMİK", "ANEMON", "ANGAJE", "ANGORA", "ANGUDİ", "ANILIK", "ANILIŞ", "ANILMA",
        "ANINDA", "ANIRIŞ", "ANIRMA", "ANIRTI", "ANISAL", "ANITLI", "ANITSI", "ANIZLI", "ANİDEN", "ANİLİK", "ANİLİN", "ANJİYO", "ANKARA", "ANLAMA", "ANLATI", "ANOFEL", "ANONİM", "ANORAK",
        "ANTANT", "ANTİKA", "ANTROK", "ANTSIZ", "APAÇIK", "APALAK", "APARAT", "APAREY", "APARMA", "APATİT", "APAYRI", "APIŞAK", "APIŞIK", "APIŞMA", "APLİKE", "APOLET", "APRECİ", "APRELİ",
        "APSELİ", "APSENT", "ARABAN", "ARAÇLI", "ARAFAT", "ARAKÇI", "ARAKLI", "ARALIK", "ARAMAK", "ARANIŞ", "ARANJE", "ARANMA", "ARANTI", "ARAPÇA", "ARAROT", "ARASAT", "ARASIZ", "ARASTA",
        "ARAŞİT", "ARAŞMA", "ARATIŞ", "ARATMA", "ARAYIŞ", "ARAYÜZ", "ARAZÖZ", "ARBEDE", "ARDİYE", "ARGALİ", "ARGOLU", "ARHAVİ", "ARICAK", "ARIKÇI", "ARILAR", "ARILIK", "ARINIK", "ARINIŞ",
        "ARINMA", "ARITIM", "ARITIŞ", "ARITMA", "ARİOSO", "ARİTMİ", "ARİYET", "ARKAİK", "ARKALI", "ARKEEN", "ARKTİK", "ARMADA", "ARMALI", "ARMONİ", "ARMUDİ", "ARNİKA", "ARPACI", "ARSLAN",
        "ARTÇIL", "ARTİST", "ARTMAK", "ARTOVA", "ARTRİT", "ARTROZ", "ARTVİN", "ARZANİ", "ARZULU", "ASALAK", "ASALET", "ASAYİŞ", "ASBEST", "ASEPSİ", "ASETAT", "ASETİK", "ASETON", "ASFALT",
        "ASGARİ", "ASILIŞ", "ASILLI", "ASILMA", "ASILTI", "ASINTI", "ASİLİK", "ASİTLİ", "ASKERİ", "ASKICI", "ASKIDA", "ASKILI", "ASLİYE", "ASMALI", "ASORTİ", "ASUMAN", "ASURCA", "ASYALI",
        "AŞARCI", "AŞERAT", "AŞERME", "AŞHANE", "AŞIKLI", "AŞILMA", "AŞINIM", "AŞINIŞ", "AŞINMA", "AŞINTI", "AŞIRIŞ", "AŞIRMA", "AŞIRTI", "AŞISIZ", "AŞİKAR", "AŞİRET", "AŞİYAN", "AŞKALE",
        "AŞKSIZ", "AŞÜFTE", "ATABEY", "ATAKUM", "ATALET", "ATALIK", "ATAMAK", "ATAMAN", "ATANIŞ", "ATANMA", "ATAVİK", "ATAYIŞ", "ATBAŞI", "ATEİST", "ATEİZM", "ATELYE", "ATEŞÇİ", "ATEŞİN",
        "ATEŞLİ", "ATIFET", "ATILIM", "ATILIŞ", "ATILMA", "ATIMCI", "ATIŞMA", "ATKILI", "ATLAMA", "ATMACA", "ATOMAL", "ATOMCU", "ATOMİK", "ATONAL", "ATÖLYE", "AVALCA", "AVAMCA", "AVANAK",
        "AVANOS", "AVANTA", "AVARCA", "AVARIZ", "AVARYA", "AVDETİ", "AVERAJ", "AVİSTO", "AVLAMA", "AVRUPA", "AVUKAT", "AVUNMA", "AVUNTU", "AVUNUŞ", "AVUTMA", "AVUTUŞ", "AYAKÇI", "AYAKLI",
        "AYAKSI", "AYAKTA", "AYARCI", "AYARLI", "AYARTI", "AYAZMA", "AYBAŞI", "AYBEAY", "AYDEDE", "AYIKMA", "AYILIK", "AYILIŞ", "AYILMA", "AYILTI", "AYINGA", "AYIPLI", "AYIRAÇ", "AYIRAN",
        "AYIRIŞ", "AYIRMA", "AYIRTI", "AYKIRI", "AYLAMA", "AYNACI", "AYNALI", "AYNISI", "AYRICA", "AYRILI", "AZALIŞ", "AZALMA", "AZAMET", "AZAPLI", "AZATLI", "AZICIK", "AZIKLI", "AZIŞMA",
        "AZITMA", "AZİMET", "AZİMLİ", "AZONAL", "AZOTLU", "AZRAİL", "ADALAT", "ADAMCI", "AHARCI", "AİTLİK", "AKİSLİ", "AKŞAMA", "ALAMOT", "ALİMCE", "ALOFON", "AMASIZ", "ANANAT", "ANSIMA",
        "ARAMCA", "ASACAK", "ATARLI", "AVADAN", "AZİZİM", "AZOTÜR", "ARUSEK", "ANTİYE", "BABACA", "BABACI", "BABALI", "BADANA", "BADEHU", "BADEMA", "BADİRE", "BADİYE", "BAĞCIK", "BAĞDAŞ",
        "CAFCAF", "CAFERİ", "CAĞLIK", "CAKACI", "CAKALI", "CAMBAZ", "CAMEVİ", "CAMGÖZ", "CAMLIK", "CAMSIZ", "ÇAÇAÇA", "ÇAĞCIL", "ÇAĞDAŞ", "ÇAĞLAR", "ÇAĞMAK", "ÇAĞNAK", "ÇAĞRIM", "ÇAKICI",
        "ÇAKILI", "ÇAKMAK", "DADACI", "DADILI", "DAĞCIL", "DAĞLIÇ", "DAĞLIK", "DAHASI", "DAHİCE", "DAHİLİ", "DAKİKA", "DALAMA", "EBABİL", "EBELİK", "EBESİZ", "EBONİT", "EBRUCU", "EBRULİ",
        "EBRULU", "ECİNNİ", "ECNEBİ", "ECZACI", "FACTOR", "FAÇALI", "FAÇETA", "FAÇUNA", "FAĞFUR", "FAHİŞE", "FAİZCİ", "FAİZLİ", "FAKFON", "FAKTÖR", "GABARİ", "GABYAR", "GADDAR", "GAFFAR",
        "GAFLET", "GAGALI", "GAHİCE", "GALEBE", "GALERİ", "GALETA", "HABEŞİ", "HACKER", "HADEME", "HADİSE", "HAFIZA", "HAFİYE", "HAİNCE", "HAKEZA", "HAKİKİ", "HAKKAK", "IHLAKA", "IKINMA",
        "IKINTI", "IKLAMA", "ILGAMA", "ILICAK", "ILIKÇA", "ILIMAK", "ILIMAN", "ILIMLI", "İADELİ", "İBADET", "İBARET", "İBİBİK", "İBİKLİ", "İBİKSİ", "İBRADI", "İBRANİ", "İCABET", "İCAPÇI",
        "JAGUAR", "JAKUZİ", "JALUZİ", "JAMBON", "JANJAN", "JAPONE", "JARGON", "JEOLOG", "JERSEY", "JİGOLO", "KABACA", "KABALA", "KABANA", "KABARA", "KABARE", "KABİLE", "KABİNE", "KAÇGÖÇ",
        "KAÇKIN", "KAÇLIK", "LAAKAL", "LABADA", "LABROS", "LADİNİ", "LADİNO", "LAEDRİ", "LAFZEN", "LAHANA", "LAHİKA", "LAHURİ", "MAAİLE", "MAARİF", "MAAŞLI", "MABLAK", "MABUDE", "MACERA",
        "MAÇUNA", "MADAMA", "MADARA", "MADENİ", "NADİDE", "NAFAKA", "NAFİLE", "NAHİYE", "NAKAVT", "NAKDEN", "NAKİSA", "NAKKAŞ", "NAKLEN", "NAKZEN", "OBUACI", "OBURCA", "OCAKÇI", "OCAKLI",
        "OCUMAK", "ODACIK", "ODALIK", "ODUNCU", "ODUNLU", "ODUNSU", "ÖBÜRKÜ", "ÖDEGEÇ", "ÖDEMEK", "ÖDEMİŞ", "ÖDEMLİ", "ÖDENCE", "ÖDENEK", "ÖDENİŞ", "ÖDENME", "ÖDENTİ", "PAÇACI", "PAÇALI",
        "PAGODA", "PAHACI", "PAHALI", "PAKLIK", "PALDIM", "PALYOŞ", "PANAMA", "PANCAR", "RABITA", "RADİKA", "RADYAN", "RADYUM", "RAFIZİ", "RAFİNE", "RAFSIZ", "RAĞBET", "RAĞMEN", "RAHİBE",
        "SAADET", "SAATÇİ", "SAATLİ", "SABAHA", "SABIKA", "SABİTE", "SABOTE", "SABURA", "SAÇMAK", "SAÇSIZ",
        "ŞABLON", "ŞAHANE", "ŞAHBAZ", "ŞAHİKA", "ŞAHLIK", "ŞAHNİŞ", "ŞAHSEN", "ŞAHTUR", "ŞAKACI", "ŞAKALI",
        "TABAAT", "TABAKA", "TABELA", "TABİAT", "TABLET", "TABURE", "TAÇSIZ", "TAFLAN", "TAFSİL", "TAĞŞİŞ",
        "UCUBİK", "UCUZCA", "UCUZCU", "UÇAKLI", "UÇURMA", "UÇURUM", "UÇURUŞ", "UÇUŞMA", "UFACIK", "UFAKÇA",
        "ÜÇAYAK", "ÜÇERLİ", "ÜÇLEME", "ÜÇÜNCÜ", "ÜÇÜZLÜ", "ÜFLEME", "ÜFÜRME", "ÜFÜRÜK", "ÜFÜRÜM", "ÜFÜRÜŞ",
        "VADELİ", "VAFTİZ", "VAHDET", "VAHŞET", "VAJİNA", "VAKETA", "VAKVAK", "VALİDE", "VAMPİR", "VANDAL",
        "YABANİ", "YAĞLIK", "YAĞMAK", "YAĞMUR", "YAĞRIN", "YAĞSIZ", "YAHUDİ", "YAKALI", "YAKARI", "YAKAZA",
        "ZAFRAN", "ZAĞARA", "ZAĞSIZ", "ZAHİRE", "ZAHİRİ", "ZAHMET", "ZAHTER", "ZAKKUM", "ZAMANE"
    )





    private val sevenLetterWords = listOf(
        "ABANMAK", "ABARTIK", "ABARTIŞ", "ABARTMA", "ABAZACA", "ABESLİK", "ABHAZCA", "ABİDEVİ", "ABLAKÇA", "ABLALIK",
        "BABACAN", "BABACIK", "BABACIL", "BABAÇKO", "BABADAĞ", "BABAEVİ", "BABAKÖŞ", "BABALIK", "BABASIZ", "BABIALİ",
        "CABADAN", "CADALOZ", "CADILIK", "CAHİLCE", "CAKASIZ", "CAMADAN", "CAMEKAN", "CAMLAMA", "CANAVAR", "CANFEZA",
        "ÇABASIZ", "ÇABUCAK", "ÇABUKÇA", "ÇAÇALIK", "ÇAÇARON", "ÇADIRCI", "ÇADIRLI", "ÇAĞANOZ", "ÇAĞILTI", "ÇAĞIRIM",
        "DADAİST", "DADAİZM", "DADANIŞ", "DADANMA", "DADILIK", "DAĞBAŞI", "DAĞDAĞA", "DAĞILIM", "DAĞILIŞ", "DAĞILMA",
        "EBELEME", "EBEVEYN", "ECEABAT", "EDEPSİZ", "EDİBANE", "EDİLGEN", "EDİLGİN", "EDİLMEK", "EDİMSEL", "EDİMSİZ",
        "FAALLİK", "FABRİKA", "FACİALI", "FAGOSİT", "FAĞFURİ", "FAHRİYE", "FAİKLİK", "FAİLLİK", "FAİZSİZ", "FAKİRCE",
        "GABAVET", "GABİLİK", "GABONLU", "GABYACI", "GACIRTI", "GAGAMSI", "GAGAVUZ", "GAİLELİ", "GAİPLİK", "GAKLAMA",
        "HABASET", "HABERCİ", "HABERLİ", "HABİTAT", "HABİTUS", "HACAMAT", "HACIAĞA", "HACILAR", "HACILIK", "HACİMCE",
        "IĞDIRLI", "IHLAMAK", "IHLAMUR", "IHTIRMA", "IKINMAK", "IKLAMAK", "ILGAMAK", "ILGARCI", "ILIKÇIL", "ILIKLIK",
        "İADESİZ", "İBLİSÇE", "İBLİSÇİ", "İBNELİK", "İBRİKÇİ", "İBRİŞİM", "İÇBÜKEY", "İÇERLEK", "İÇERMEK", "İÇGÖREÇ",
        "JAKARLI", "JAKOBEN", "JAPONCA", "JELATİN", "JENERİK", "JENOSİT", "JEODEZİ", "JEOLOJİ", "JETONCU", "JİLETLİ",
        "KABADÜZ", "KABAHAT", "KABAKÇI", "KABALAK", "KABALIK", "KABARIK", "KABARIŞ", "KABARMA", "KABARTI", "KABAŞİŞ",
        "LACEREM", "LADENLİ", "LADESLİ", "LAFAZAN", "LAFLAMA", "LAĞIMCI", "LAHAVLE", "LAHZADA", "LAİKLİK", "LAİSİZM",
        "MAAŞSIZ", "MABEYİN", "MACARCA", "MACUNCU", "MAÇOLUK", "MADALYA", "MADDECİ", "MADEMKİ", "MADENCİ", "MADENSİ",
        "NABEKAR", "NADANCA", "NADASLI", "NADİRAT", "NADİREN", "NAGEHAN", "NAĞMELİ", "NAHIRCI", "NAİPLİK", "NAKARAT",
        "OBABAŞI", "OBELİSK", "OBEZİTE", "OBEZLİK", "OBRUKLU", "OBSESİF", "OBURLUK", "OCAKLIK", "OCAKSIZ", "ODABAŞI",
        "ÖÇLENME", "ÖDEMELİ", "ÖDENMEK", "ÖDEŞMEK", "ÖDETMEK", "ÖDEVCİL", "ÖDLEKÇE", "ÖDÜNSÜZ", "ÖFKESİZ", "ÖĞLENCİ",
        "PABUÇÇU", "PABUÇLU", "PAÇALIK", "PAÇARIZ", "PAÇASIZ", "PAÇAVRA", "PADALYA", "PADİŞAH", "PAFTALI", "PAHLAMA",
        "RABBANİ", "RABBENA", "RADANSA", "RADARCI", "RADİKAL", "RADYOCU", "RAFADAN", "RAFİNAJ", "RAFTİNG", "RAHATÇA",
        "SAATİNE", "SAATLİK", "SABAHÇI", "SABAHKİ", "SABIRLA", "SABIRLI", "SABOTAJ", "SABUNCU", "SABUNLU", "SACAYAK",
        "ŞAHESER", "ŞAHİNCİ", "ŞAHİTLİ", "ŞAHTERE", "ŞAİBELİ", "ŞAİRANE", "ŞAİRLİK", "ŞAKADAN", "ŞAKASIZ", "ŞAKAYIK",
        "TAACCÜP", "TAADDÜT", "TAAFFÜN", "TAAHHÜT", "TAALLUK", "TAAMMÜM", "TAAMMÜT", "TAANNÜT", "TAARRUZ", "TAASSUP",
        "UCUZLUK", "UCUZUNA", "UÇAKSIZ", "UÇARICA", "UÇKURLU", "UÇLANMA", "UÇUKLUK", "UÇURMAK", "UÇURTMA", "UÇUŞMAK",
        "ÜNVANLI", "ÜCRETLİ", "ÜÇBUDAK", "ÜÇÇATAL", "ÜÇKAĞIT", "ÜÇLEMEK", "ÜÇLEŞME", "ÜÇTEKER", "ÜÇTELLİ", "ÜÇÜNCÜL",
        "VABESTE", "VADESİZ", "VADETME", "VAGONET", "VAHAMET", "VAHİLİK", "VAHŞİCE", "VAİZLİK", "VAKARLI", "VAKFİYE",
        "YABANCI", "YABANIL", "YABANSI", "YADIRGI", "YADİGAR", "YADSIMA", "YAĞHANE", "YAĞILIK", "YAĞILTI", "YAĞIMSI",
        "ZABİTAN", "ZADEGAN", "ZAFİYET", "ZAĞANOS", "ZAĞARCI", "ZAĞLAMA", "ZAHİRDE", "ZAHİREN", "ZALİMCE", "ZAMANLA"
    )


    private val eightLetterWords = listOf(
        "ABACILIK", "ABAJURCU", "ABAJURLU", "ABANDONE", "ABARTICI", "ABARTILI", "ABARTMAK", "ABDALLIK", "ABDESTLİ", "ABDİACİZ",
        "BABAANNE", "BABAESKİ", "BABAİLİK", "BABAYANİ", "BACABAŞI", "BACAKLIK", "BACAKSIZ", "BAÇÇILIK", "BADANACI", "BADANALI",
        "CAFCAFLI", "CAHİLANE", "CAHİLLİK", "CAMCILIK", "CAMLAMAK", "CAMLANMA", "CAMLAŞMA", "CAMLATMA", "CANANLIK", "CANCAĞIZ",
        "ÇABALAMA", "ÇABUKLUK", "ÇAĞIRMAK", "ÇAĞIRTMA", "ÇAĞLAMAK", "ÇAĞLAYAN", "ÇAĞLAYIK", "ÇAĞLAYIŞ", "ÇAĞRILIK", "ÇAĞRILIŞ",
        "DADANMAK", "DADAŞLIK", "DAĞARCIK", "DAĞCILIK", "DAĞILMAK", "DAĞITICI", "DAĞITMAK", "DAĞLAMAK", "DAĞLANIŞ", "DAĞLANMA",
        "EBEDİLİK", "EBEDİYEN", "EBEDİYET", "EBELEMEK", "EBELEYİŞ", "EBLEHLİK", "EBRULAMA", "EBUSSUUT", "EDEBİLME", "EDEBİYAT",
        "FAALİYET", "FAÇETALI", "FAHİŞLİK", "FAHRİLİK", "FAİKİYET", "FAKİRANE", "FAKİRİZM", "FAKİRLİK", "FAKSLAMA", "FAKTİTİF",
        "GABARDİN", "GADDARCA", "GADİRLİK", "GADRETME", "GADROLMA", "GAFİLANE", "GAFİLLİK", "GAGALAMA", "GAİLESİZ", "GAKLAMAK",
        "HABANERA", "HABERDAR", "HABERLİK", "HABERSİZ", "HABİSLİK", "HACCETME", "HACIYOLU", "HACİMSİZ", "HACZETME", "HAÇLAMAK",
        "IHTIRMAK", "ILGINCAR", "IRAKSAMA", "IRALAMAK", "IRGALAMA", "IRGANMAK", "IRGATLIK", "IRKÇILIK", "ISIÖLÇER", "ISIRILMA",
        "İBLİSANE", "İBRANAME", "İBRANİCE", "İBRETLİK", "İBRİKTAR", "İCABINDA", "İCRAATÇI", "İÇEBİLME", "İÇERİKLİ", "İÇERLEME",
        "JAKUZİLİ", "JALUZİLİ", "JANDARMA", "JANJANLI", "JARTİYER", "JELLEŞME", "JEOFİZİK", "JEOKİMYA", "JEOLOJİK", "JEOTERMİ",
        "KABADAYI", "KABAKLIK", "KABALACI", "KABALİST", "KABARALI", "KABARCIK", "KABARECİ", "KABARMAK", "KABARTMA", "KABIZLIK",
        "LABİRENT", "LABORANT", "LABRADOR", "LACİVERT", "LAÇKALIK", "LAEDRİYE", "LAFÇILIK", "LAFLAMAK", "LAĞVETME", "LAĞVOLMA",
        "MAALESEF", "MAARİFÇİ", "MACARLIK", "MACERACI", "MACERALI", "MACUNLUK", "MADALYON", "MADDELİK", "MADDESEL", "MADDETEN",
        "NAÇARLIK", "NAÇİZANE", "NAÇİZLİK", "NADANLIK", "NADASLIK", "NADİRLİK", "NAFTALİN", "NAĞMESİZ", "NAHİFLİK", "NAHOŞLUK",
        "OBJEKTİF", "OBSESYON", "OCAKBAŞI", "ODACILIK", "ODAKLAMA", "ODUNUMSU", "OĞLANCIK", "OĞLANEVİ", "OKKALAMA", "OKLANMAK",
        "ÖBÜRLERİ", "ÖÇLENMEK", "ÖDENEKLİ", "ÖDETİLME", "ÖDLEKLİK", "ÖDÜNLEME", "ÖĞLEÜSTÜ", "ÖĞRENMEK", "ÖĞRETİCİ", "ÖĞRETMEK",
        "PABUÇLUK", "PABUÇSUZ", "PADİŞAHİ", "PAFTASIZ", "PAGANİZM", "PAHALICA", "PAHASINA", "PAHLAMAK", "PAKLAMAK", "PAKLANMA",
        "RABITALI", "RADYATÖR", "RADYOEVİ", "RADYOLOG", "RAFİNERİ", "RAĞBETLİ", "RAHATLIK", "RAHATSIZ", "RAHİPLİK", "RAHMETLİ",
        "SAADETLE", "SAADETLİ", "SAATİNDE", "SABAHLIK", "SABAHTAN", "SABIKALI", "SABIRSIZ", "SABİTLİK", "SABREDİŞ", "SABRETME",
        "ŞABANLIK", "ŞABANÖZÜ", "ŞABLONCU", "ŞADIRVAN", "ŞAFİİLİK", "ŞAHİNBEY", "ŞAHİNLER", "ŞAHİTLİK", "ŞAHİTSİZ", "ŞAHLANIŞ",
        "TABAKALI", "TABAKLIK", "TABAKSIZ", "TABANLIK", "TABANSIZ", "TABANVAY", "TABASBUS", "TABELACI", "TABELALI", "TABETMEK",
        "UBUDİYET", "UCUZLAMA", "UÇABİLME", "UÇANKALE", "UÇARILIK", "UÇKURLUK", "UÇKURSUZ", "UÇLANMAK", "UÇTUUÇTU", "UÇUCULUK",
        "ÜNVANSIZ", "ÜCRETSİZ", "ÜÇÇEYREK", "ÜÇLEŞMEK", "ÜÇÜZLEME", "ÜFLEMELİ", "ÜFLENMEK", "ÜFLETMEK", "ÜFÜRÜKÇÜ", "ÜLEŞİLME",
        "VADETMEK", "VAGOTONİ", "VAHİMLİK", "VAHŞİLİK", "VAKARSIZ", "VAKFEDİŞ", "VAKFETME", "VAKIFLIK", "VAKİTSİZ", "VAKLAMAK",
        "YABALAMA", "YABANCIL", "YABANİCE", "YABANLIK", "YADSIMAK", "YADSINMA", "YAĞCILIK", "YAĞDIRMA", "YAĞIŞSIZ", "YAĞLAMAK",
        "ZABİTLİK", "ZAĞARLIK", "ZAĞCILIK", "ZAĞLAMAK", "ZAHİTLİK", "ZAHMETLİ", "ZAKKUMLU", "ZALİMANE", "ZALİMLİK", "ZAMANDAŞ"
    )



    fun getRandomWord(wordLength: Int): String {
        return when (wordLength) {
            5 -> words.random()
            6 -> sixLetterWords.random()
            7 -> sevenLetterWords.random()
            8 -> eightLetterWords.random()
            else -> throw IllegalArgumentException("Unsupported word length: $wordLength")
        }
    }
}