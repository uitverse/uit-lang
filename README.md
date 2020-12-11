# UIT

UIT Interpreter for Techie - a simple interpreter written in PHP for PseudoCode like Programming Language.

## HOW TO RUN

ဘယ်လို run မလဲ ... ထုံးစံအတိုင်း `git clone` ပြီး run ရုံပါပဲ။ `php 8` တော့လိုလိမ့်မယ်။

```shell
git clone https://github.com/heinthanth/uit
cd uit
composer install
php uit.php script # to run script
php uit.php # to run REPL
```

## EXAMPLES

လောလောဆယ် development အဆင့်မှာပဲရှိသေးတယ်ဆိုတော့ အများကြီး မရသေးဘူး။ ဂဏန်း ပေါင်း၊ နုတ်၊ မြောက်၊ စား တော့ ရပြီဗျ။

file လေး တစ် file ရေးကြည့်ပေါ့။ အောက်က အတိုင်းပေါ့ ဥပမာ - `hello.uit` ဆိုပြီး save လိုက်မယ်။

```text
4 + 5 * ( 4 + 3 / 2 ) - 4 
```

ပြီးရင် အောက်ကအတိုင်း run လိုက်မယ်ဆိုရင် `27.5` ဆိုပြီး result ထွက်လာမှာဖြစ်တယ်။

```shell
php uit.php hello.uit
```

ဒါမှမဟုတ် file မလုပ်ဘဲ `REPL` နဲ့ run လည်းရပါတယ်။

```shell
php uit.php
```

လောလောဆယ် error handling မထည့်ထားတော့ syntax error ရှိရင် error ပြပြီး exit ဖြစ်သွားလိမ့်မယ်။ အဆင်ပြေပါစေဗျာ။

## NOTES

အခု number calculation မှာဆိုရင် `+`, `-`, `*`, `/`, `%`,`^` operators တွေသုံးလို့ရပါတယ်။

## FAQ

### WHY I MADE THIS

လုပ်ဖြစ်တဲ့ အကြောင်းအရင်းကတော့ ထွေထွေထူးထူးမရှိပါဘူး။ ကျွန်တော် first year တုန်းက တစ်ချို့ သူငယ်ချင်းတွေက ဆေးကျောင်းတက်ချင်ပေမယ့် အခြေအနေတစ်ခုကြောင့် UIT ရောက်လာကြတဲ့အခါ အဆင်မပြေတာလေးတွေတွေ့ရတယ်ဗျ။\
ကျောင်းမှာ pseudo-code သင်တဲ့အခါ ကျွန်တော်တော့ programming နဲ့ ရင်းနှီးတဲ့အတွက် နားလည်တယ်ဗျ။ သူတို့ကျတော့ မရင်းနှီးတော့ မမြင်သာသလိုဖြစ်နေရော။ အဲ့အချိန်ကတည်းက pseudo-code ကိုသာ programming language တစ်ခုလို run လို့ရမယ်ဆိုရင် အဆင်ပြေလိမ့်မယ်ဆိုပြီး တွေးမိတယ်။\
အဲ့ကတည်းက interpreter ရေးဖို့ကြိုးစားခဲ့တာ အခုထိပဲဆိုပါစို့။

### WHY NAMED 'UIT'

အကြောင်းပြချက်မရှိပါဘူး။ ဒီအတိုင်းပဲပေးလိုက်တာ။ `PHP HyperText Processor (PHP)` ဆိုတဲ့ Recursive Acronym ကို သဘောကျတာရယ်၊ ကျောင်းနာမည်နဲ့လည်း တိုက်ဆိုင်တာရယ်ကြောင့် `UIT - Interpreter for Techie ( UIT )`  ဆိုပြီးပေးဖြစ်သွားပါတယ်။

### WHY NOT CHOOSING OTHER LANGUAGE

ဟဟ၊ ဘာလို့ တခြား language မသုံးလဲဆိုတော့ ကျွန်တော်က PHP fan ဗျ။ တခြား language တွေထက်စာရင် ကျွန်တော်အကျွမ်းကျင်ဆုံးက PHP ပဲ။ ပြီးတော့ လူတစ်ချို့က PHP ကနှိမ်ကြတယ်ဗျ။ PHP လည်း ကောင်းပါသေးတယ်လို့ သက်သေပြချင်တာလည်း ပါတာပေါ့။\
တခြား language ( C, C++ ) တို့နဲ့ ပြန်ရေးဖို့တော့ ကြိုးစားနေပါတယ်။ skill တော့လိုသေးတာပေါ့။

## LICENSE

ရှင်းရှင်းပါပဲ MIT license ပဲသုံးထားတယ်။ အသေးစိတ်ကတော့ [License](LICENSE) file ကိုပဲသွားကြည့်လိုက်ပါတော့။
