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
        "KABAN", "KABİN", "KABUL", "KADIN", "KALEM", "KALIN",  "KARIN", "KARŞI", "KASIM", "KAVUN", "KAZAK",
        "KEKİK", "KEMER", "KİBİR", "KİBAR", "KİLER", "KİRAZ", "KİRPİ", "KIZAK",
        "KOÇAK", "KOYUN", "KOYUN", "KREDİ", "KURAN", "KUMRU",  "KUŞAK", "KUTLU", "KÜÇÜK",
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
        "MEŞHUR", "NEŞELİ",
        "YAPRAK",
    )

    fun getRandomWord(length: Int = 5): String {
        return if (length == 5) {
            words[Random.nextInt(words.size)]
        } else {
            sixLetterWords[Random.nextInt(sixLetterWords.size)]
        }
    }
}