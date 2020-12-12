# UIT

[![MIT license](https://img.shields.io/badge/License-MIT-green.svg)](https://lbesson.mit-license.org/)
[![Line Count](https://img.shields.io/static/v1.svg?label=Line+of+PHP+code&message=1781&color=blue)](https://shields.io/)

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

## MILE STONE

- [x] Number Type
- [x] Arithmetic Operation (`+`, `-`, `*`, `/`, `%`, `^`)
- [x] Variables
- [x] Null Type  
- [x] Boolean Type
- [x] Logical Operator ( `AND`, `OR`, `==`, `<>`, `<`, `<=`, `>`, `>=` )
- [x] Condition ( `IF` / `ELSEIF` / `ELSE` )
- [x] Loop ( `FOR`, `WHILE` )
- [x] Function ( without RETURN )
- [ ] String Type
- [ ] Array Type
- [ ] Built-in Functions
- [ ] Multiple Line Statements
- [ ] Comments

## EXAMPLES

လောလောဆယ် development အဆင့်မှာပဲရှိသေးတယ်ဆိုတော့ အများကြီး မရသေးဘူး။ ဂဏန်း ပေါင်း၊ နုတ်၊ မြောက်၊ စား ရမယ်။ number
variable ရမယ်။ logical operator တွေရမယ်။ loop ရမယ်။ ကျန်တာတွေ ထပ် implement လုပ်နေတုန်းပါပဲ။

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

number calculation မှာဆိုရင် `+`, `-`, `*`, `/`, `%`,`^` operators တွေသုံးလို့ရပါတယ်။

```text
10 / 2 + 4 % ( 2 + 2 ) ^ 2
```

variable assignment မှာဆို Number Type ရမယ်။

```text
Num foo = 3
foo + 3
```

logical operator (AND, OR) တွေရမယ်။

```text
4 == 4
5 == 1 OR 4 == 4
```

loop မှာဆို WHILE နဲ့ FOR ရမယ်။

```text
FOR i = 1 TO 6 step 3 DO Num result = i ENDFOR
```

function ရမယ်။ return ကတော့ လုပ်နေတုန်းပါပဲ။

```text
func demo(Num a, Num b) a + b stop
demo(3, 5)
```

## FAQ

### WHY I MADE THIS

လုပ်ဖြစ်တဲ့ အကြောင်းအရင်းကတော့ ထွေထွေထူးထူးမရှိပါဘူး။ ကျွန်တော် first year တုန်းက တစ်ချို့ သူငယ်ချင်းတွေက
ဆေးကျောင်းတက်ချင်ပေမယ့် အခြေအနေတစ်ခုကြောင့် UIT ရောက်လာကြတဲ့အခါ အဆင်မပြေတာလေးတွေတွေ့ရတယ်ဗျ။\
ကျောင်းမှာ pseudo-code သင်တဲ့အခါ ကျွန်တော်တော့ programming နဲ့ ရင်းနှီးတဲ့အတွက် နားလည်တယ်ဗျ။ သူတို့ကျတော့ မရင်းနှီးတော့
မမြင်သာသလိုဖြစ်နေရော။ အဲ့အချိန်ကတည်းက pseudo-code ကိုသာ programming language တစ်ခုလို run လို့ရမယ်ဆိုရင်
အဆင်ပြေလိမ့်မယ်ဆိုပြီး တွေးမိတယ်။\
အဲ့ကတည်းက interpreter ရေးဖို့ကြိုးစားခဲ့တာ အခုထိပဲဆိုပါစို့။\
တကယ်တော့ pseudocode နဲ့ python သိပ်မကွာတော့ python သုံးလည်း ရတာပဲကိုလို့ ပြောကောင်းပြောလိမ့်မယ်။ ဒါပေမယ့် ဒီ interpreter က ကျွန့်တော်ရဲ့ အပျော် project ပါ။ ကျွန်တော် အားကျရတဲ့ ဆရာ သက်ခိုင် လို VM တစ်ခုထိ မရေးနိုင်သေးပေမယ့် ဒီ interpreter project ကပဲ အများကြီးလေ့လာဖြစ်စေခဲ့ပါတယ်။

### WHY NAMED 'UIT'

အကြောင်းပြချက်မရှိပါဘူး။ ဒီအတိုင်းပဲပေးလိုက်တာ။ `PHP HyperText Processor (PHP)` ဆိုတဲ့ Recursive Acronym ကို
သဘောကျတာရယ်၊ ကျောင်းနာမည်နဲ့လည်း တိုက်ဆိုင်တာရယ်ကြောင့် `UIT - Interpreter for Techie ( UIT )`  ဆိုပြီးပေးဖြစ်သွားပါတယ်။

### WHY NOT CHOOSING OTHER LANGUAGE

ဟဟ၊ ဘာလို့ တခြား language မသုံးလဲဆိုတော့ ကျွန်တော်က PHP fan ဗျ။ တခြား language တွေထက်စာရင် ကျွန်တော်အကျွမ်းကျင်ဆုံးက PHP
ပဲ။ ပြီးတော့ လူတစ်ချို့က PHP ကနှိမ်ကြတယ်ဗျ။ PHP လည်း ကောင်းပါသေးတယ်လို့ သက်သေပြချင်တာလည်း ပါတာပေါ့။\
တခြား language ( C, C++ ) တို့နဲ့ ပြန်ရေးဖို့တော့ ကြိုးစားနေပါတယ်။ skill တော့လိုသေးတာပေါ့။ ပြီးတော့ ကျွန်တော်ရေးထားတဲ့ပုံစံအရ interface အတုတွေ သုံးထားတော့ dynamic language ဖြစ်ဖို့လိုနေတယ်ဗျ။

## LICENSE

ရှင်းရှင်းပါပဲ MIT license ပဲသုံးထားတယ်။ အသေးစိတ်ကတော့ [License](LICENSE) file ကိုပဲသွားကြည့်လိုက်ပါတော့။
