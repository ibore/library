package me.ibore.utils

import android.content.Context
import android.content.res.Resources
import android.telephony.TelephonyManager
import java.util.*

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2019/06/11
 * desc  : utils about country code
</pre> *
 */
object CountryUtils {

    private val countryCodeMap: HashMap<String, String> = HashMap(256)

    /**
     * Return the country code by sim card.
     *
     * @param defaultValue The default value.
     * @return the country code
     */
    fun getCountryCodeBySim(defaultValue: String): String {
        return countryCodeMap[countryBySim] ?: return defaultValue
    }

    /**
     * Return the country code by system language.
     *
     * @param defaultValue The default value.
     * @return the country code
     */
    fun getCountryCodeByLanguage(defaultValue: String): String {
        return countryCodeMap[countryByLanguage] ?: return defaultValue
    }

    /**
     * Return the country by sim card.
     *
     * @return the country
     */
    val countryBySim: String
        get() {
            val manager = Utils.app.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
            return manager?.simCountryIso?.toUpperCase(Locale.ROOT) ?: ""
        }

    /**
     * Return the country by system language.
     *
     * @return the country
     */
    val countryByLanguage: String
        get() = Resources.getSystem().configuration.locale.country

    init {
        countryCodeMap["AL"] = "+355"
        countryCodeMap["DZ"] = "+213"
        countryCodeMap["AF"] = "+93"
        countryCodeMap["AR"] = "+54"
        countryCodeMap["AE"] = "+971"
        countryCodeMap["AW"] = "+297"
        countryCodeMap["OM"] = "+968"
        countryCodeMap["AZ"] = "+994"
        countryCodeMap["AC"] = "+247"
        countryCodeMap["EG"] = "+20"
        countryCodeMap["ET"] = "+251"
        countryCodeMap["IE"] = "+353"
        countryCodeMap["EE"] = "+372"
        countryCodeMap["AD"] = "+376"
        countryCodeMap["AO"] = "+244"
        countryCodeMap["AI"] = "+1"
        countryCodeMap["AG"] = "+1"
        countryCodeMap["AT"] = "+43"
        countryCodeMap["AX"] = "+358"
        countryCodeMap["AU"] = "+61"
        countryCodeMap["BB"] = "+1"
        countryCodeMap["PG"] = "+675"
        countryCodeMap["BS"] = "+1"
        countryCodeMap["PK"] = "+92"
        countryCodeMap["PY"] = "+595"
        countryCodeMap["PS"] = "+970"
        countryCodeMap["BH"] = "+973"
        countryCodeMap["PA"] = "+507"
        countryCodeMap["BR"] = "+55"
        countryCodeMap["BY"] = "+375"
        countryCodeMap["BM"] = "+1"
        countryCodeMap["BG"] = "+359"
        countryCodeMap["MP"] = "+1"
        countryCodeMap["BJ"] = "+229"
        countryCodeMap["BE"] = "+32"
        countryCodeMap["IS"] = "+354"
        countryCodeMap["PR"] = "+1"
        countryCodeMap["PL"] = "+48"
        countryCodeMap["BA"] = "+387"
        countryCodeMap["BO"] = "+591"
        countryCodeMap["BZ"] = "+501"
        countryCodeMap["BW"] = "+267"
        countryCodeMap["BT"] = "+975"
        countryCodeMap["BF"] = "+226"
        countryCodeMap["BI"] = "+257"
        countryCodeMap["KP"] = "+850"
        countryCodeMap["GQ"] = "+240"
        countryCodeMap["DK"] = "+45"
        countryCodeMap["DE"] = "+49"
        countryCodeMap["TL"] = "+670"
        countryCodeMap["TG"] = "+228"
        countryCodeMap["DO"] = "+1"
        countryCodeMap["DM"] = "+1"
        countryCodeMap["RU"] = "+7"
        countryCodeMap["EC"] = "+593"
        countryCodeMap["ER"] = "+291"
        countryCodeMap["FR"] = "+33"
        countryCodeMap["FO"] = "+298"
        countryCodeMap["PF"] = "+689"
        countryCodeMap["GF"] = "+594"
        countryCodeMap["VA"] = "+39"
        countryCodeMap["PH"] = "+63"
        countryCodeMap["FJ"] = "+679"
        countryCodeMap["FI"] = "+358"
        countryCodeMap["CV"] = "+238"
        countryCodeMap["FK"] = "+500"
        countryCodeMap["GM"] = "+220"
        countryCodeMap["CG"] = "+242"
        countryCodeMap["CD"] = "+243"
        countryCodeMap["CO"] = "+57"
        countryCodeMap["CR"] = "+506"
        countryCodeMap["GG"] = "+44"
        countryCodeMap["GD"] = "+1"
        countryCodeMap["GL"] = "+299"
        countryCodeMap["GE"] = "+995"
        countryCodeMap["CU"] = "+53"
        countryCodeMap["GP"] = "+590"
        countryCodeMap["GU"] = "+1"
        countryCodeMap["GY"] = "+592"
        countryCodeMap["KZ"] = "+7"
        countryCodeMap["HT"] = "+509"
        countryCodeMap["KR"] = "+82"
        countryCodeMap["NL"] = "+31"
        countryCodeMap["BQ"] = "+599"
        countryCodeMap["SX"] = "+1"
        countryCodeMap["ME"] = "+382"
        countryCodeMap["HN"] = "+504"
        countryCodeMap["KI"] = "+686"
        countryCodeMap["DJ"] = "+253"
        countryCodeMap["KG"] = "+996"
        countryCodeMap["GN"] = "+224"
        countryCodeMap["GW"] = "+245"
        countryCodeMap["CA"] = "+1"
        countryCodeMap["GH"] = "+233"
        countryCodeMap["GA"] = "+241"
        countryCodeMap["KH"] = "+855"
        countryCodeMap["CZ"] = "+420"
        countryCodeMap["ZW"] = "+263"
        countryCodeMap["CM"] = "+237"
        countryCodeMap["QA"] = "+974"
        countryCodeMap["KY"] = "+1"
        countryCodeMap["CC"] = "+61"
        countryCodeMap["KM"] = "+269"
        countryCodeMap["XK"] = "+383"
        countryCodeMap["CI"] = "+225"
        countryCodeMap["KW"] = "+965"
        countryCodeMap["HR"] = "+385"
        countryCodeMap["KE"] = "+254"
        countryCodeMap["CK"] = "+682"
        countryCodeMap["CW"] = "+599"
        countryCodeMap["LV"] = "+371"
        countryCodeMap["LS"] = "+266"
        countryCodeMap["LA"] = "+856"
        countryCodeMap["LB"] = "+961"
        countryCodeMap["LT"] = "+370"
        countryCodeMap["LR"] = "+231"
        countryCodeMap["LY"] = "+218"
        countryCodeMap["LI"] = "+423"
        countryCodeMap["RE"] = "+262"
        countryCodeMap["LU"] = "+352"
        countryCodeMap["RW"] = "+250"
        countryCodeMap["RO"] = "+40"
        countryCodeMap["MG"] = "+261"
        countryCodeMap["IM"] = "+44"
        countryCodeMap["MV"] = "+960"
        countryCodeMap["MT"] = "+356"
        countryCodeMap["MW"] = "+265"
        countryCodeMap["MY"] = "+60"
        countryCodeMap["ML"] = "+223"
        countryCodeMap["MK"] = "+389"
        countryCodeMap["MH"] = "+692"
        countryCodeMap["MQ"] = "+596"
        countryCodeMap["YT"] = "+262"
        countryCodeMap["MU"] = "+230"
        countryCodeMap["MR"] = "+222"
        countryCodeMap["US"] = "+1"
        countryCodeMap["AS"] = "+1"
        countryCodeMap["VI"] = "+1"
        countryCodeMap["MN"] = "+976"
        countryCodeMap["MS"] = "+1"
        countryCodeMap["BD"] = "+880"
        countryCodeMap["PE"] = "+51"
        countryCodeMap["FM"] = "+691"
        countryCodeMap["MM"] = "+95"
        countryCodeMap["MD"] = "+373"
        countryCodeMap["MA"] = "+212"
        countryCodeMap["MC"] = "+377"
        countryCodeMap["MZ"] = "+258"
        countryCodeMap["MX"] = "+52"
        countryCodeMap["NA"] = "+264"
        countryCodeMap["ZA"] = "+27"
        countryCodeMap["SS"] = "+211"
        countryCodeMap["NR"] = "+674"
        countryCodeMap["NI"] = "+505"
        countryCodeMap["NP"] = "+977"
        countryCodeMap["NE"] = "+227"
        countryCodeMap["NG"] = "+234"
        countryCodeMap["NU"] = "+683"
        countryCodeMap["NO"] = "+47"
        countryCodeMap["NF"] = "+672"
        countryCodeMap["PW"] = "+680"
        countryCodeMap["PT"] = "+351"
        countryCodeMap["JP"] = "+81"
        countryCodeMap["SE"] = "+46"
        countryCodeMap["CH"] = "+41"
        countryCodeMap["SV"] = "+503"
        countryCodeMap["WS"] = "+685"
        countryCodeMap["RS"] = "+381"
        countryCodeMap["SL"] = "+232"
        countryCodeMap["SN"] = "+221"
        countryCodeMap["CY"] = "+357"
        countryCodeMap["SC"] = "+248"
        countryCodeMap["SA"] = "+966"
        countryCodeMap["BL"] = "+590"
        countryCodeMap["CX"] = "+61"
        countryCodeMap["ST"] = "+239"
        countryCodeMap["SH"] = "+290"
        countryCodeMap["PN"] = "+870"
        countryCodeMap["KN"] = "+1"
        countryCodeMap["LC"] = "+1"
        countryCodeMap["MF"] = "+590"
        countryCodeMap["SM"] = "+378"
        countryCodeMap["PM"] = "+508"
        countryCodeMap["VC"] = "+1"
        countryCodeMap["LK"] = "+94"
        countryCodeMap["SK"] = "+421"
        countryCodeMap["SI"] = "+386"
        countryCodeMap["SJ"] = "+47"
        countryCodeMap["SZ"] = "+268"
        countryCodeMap["SD"] = "+249"
        countryCodeMap["SR"] = "+597"
        countryCodeMap["SB"] = "+677"
        countryCodeMap["SO"] = "+252"
        countryCodeMap["TJ"] = "+992"
        countryCodeMap["TH"] = "+66"
        countryCodeMap["TZ"] = "+255"
        countryCodeMap["TO"] = "+676"
        countryCodeMap["TC"] = "+1"
        countryCodeMap["TA"] = "+290"
        countryCodeMap["TT"] = "+1"
        countryCodeMap["TN"] = "+216"
        countryCodeMap["TV"] = "+688"
        countryCodeMap["TR"] = "+90"
        countryCodeMap["TM"] = "+993"
        countryCodeMap["TK"] = "+690"
        countryCodeMap["WF"] = "+681"
        countryCodeMap["VU"] = "+678"
        countryCodeMap["GT"] = "+502"
        countryCodeMap["VE"] = "+58"
        countryCodeMap["BN"] = "+673"
        countryCodeMap["UG"] = "+256"
        countryCodeMap["UA"] = "+380"
        countryCodeMap["UY"] = "+598"
        countryCodeMap["UZ"] = "+998"
        countryCodeMap["GR"] = "+30"
        countryCodeMap["ES"] = "+34"
        countryCodeMap["EH"] = "+212"
        countryCodeMap["SG"] = "+65"
        countryCodeMap["NC"] = "+687"
        countryCodeMap["NZ"] = "+64"
        countryCodeMap["HU"] = "+36"
        countryCodeMap["SY"] = "+963"
        countryCodeMap["JM"] = "+1"
        countryCodeMap["AM"] = "+374"
        countryCodeMap["YE"] = "+967"
        countryCodeMap["IQ"] = "+964"
        countryCodeMap["UM"] = "+1"
        countryCodeMap["IR"] = "+98"
        countryCodeMap["IL"] = "+972"
        countryCodeMap["IT"] = "+39"
        countryCodeMap["IN"] = "+91"
        countryCodeMap["ID"] = "+62"
        countryCodeMap["GB"] = "+44"
        countryCodeMap["VG"] = "+1"
        countryCodeMap["IO"] = "+246"
        countryCodeMap["JO"] = "+962"
        countryCodeMap["VN"] = "+84"
        countryCodeMap["ZM"] = "+260"
        countryCodeMap["JE"] = "+44"
        countryCodeMap["TD"] = "+235"
        countryCodeMap["GI"] = "+350"
        countryCodeMap["CL"] = "+56"
        countryCodeMap["CF"] = "+236"
        countryCodeMap["CN"] = "+86"
        countryCodeMap["MO"] = "+853"
        countryCodeMap["TW"] = "+886"
        countryCodeMap["HK"] = "+852"
    }

}