import string

test_list = []
test_list = list(string.ascii_uppercase) + list(string.ascii_lowercase)


def generateKey(string, key):
    key = list(key)
    if len(string) == len(key):
        return (key)
    elif len(string) < len(key):
        key = key[0:len(string)]
    else:
        for i in range(len(string) - len(key)):
            key.append(key[i % len(key)])
    return ("".join(key))


special_chars1 = ['^', '~', ':', '<', '>', '{', '}', '[', ']', '~', '`', '|']


def remove_special_chars(string):
    for char in special_chars1:
        string = string.replace(char, '')
    return string


# encryption code strating from here
schar_to_word = {
    "!": "Zy",
    "@": "Qo",
    "+": "Jk",
    "-": "Vp",
    "*": "zy",
    "/": "qx",
    "?": "jp",
    "%": "vf",
    "_": "XF",
    "=": "QX",
    ",": "BX",
    ".": "VQ",
    ' " ': "JZ",
    " ' ": "ZQ",
    "(": "XY",
    ")": "RJ",
    "#": "VD",
    "$": "WN"
}

special_chars = ['@', '!', '^', '&', '/', '\\', '#', ',', '+', '(', ')', '$', '~', '%', '.', "'", '"', ':', '*', '?',
                 '<', '>', '{', '}', '=', '-', '_', '[', ']', '~', '`', '|']

digit_to_word = {
    "0": "zo",
    "1": "oe",
    "2": "tw",
    "3": "thr",
    "4": "fr",
    "5": "fe",
    "6": "ix",
    "7": "sev",
    "8": "eht",
    "9": "nin"
}


def specialchartoword(message):
    message = remove_special_chars(message)
    for word, schar in word_to_schar.items():
        message = message.replace(schar, word)
    return message


def convert_digits_to_words(string):
    for number, word in digit_to_word.items():
        string = string.replace(number, word)
    return string


def cipherText(string, key):
    cipher_text = []
    for i in range(len(string)):
        x = (test_list.index(string[i]) + test_list.index(key[i])) % 52
        cipher_text.append(test_list[x])
    return ("".join(cipher_text))


def codes_table(char):
    table = {
        "A": 11, "B": 21, "C": 31, "D": 41, "E": 51, "F": 61, "G": 71, "H": 81,
        "I": 12, "J": 22, "K": 32, "L": 42, "M": 52, "N": 62, "O": 72, "P": 82,
        "Q": 13, "R": 23, "S": 33, "T": 43, "U": 53, "V": 63, "W": 73, "X": 83,
        "Y": 14, "Z": 24, "a": 34, "b": 44, "c": 54, "d": 64, "e": 74, "f": 84,
        "g": 15, "h": 25, "i": 35, "j": 45, "k": 55, "l": 65, "m": 75, "n": 85,
        "o": 16, "p": 26, "q": 36, "r": 46, "s": 56, "t": 66, "u": 76, "v": 86,
        "w": 17, "x": 27, "y": 37, "z": 47,
    }
    return table[char]


def encoding(text):
    text, finished_text = text, ""
    for symbol in text:
        if symbol.isalpha():
            finished_text += str(codes_table(symbol)) + " "

    return finished_text


# decryption code strating from here
word_to_digit = {
    "zo": "0",
    "oe": "1",
    "tw": "2",
    "thr": "3",
    "fr": "4",
    "fe": "5",
    "ix": "6",
    "sev": "7",
    "eht": "8",
    "nin": "9"
}

word_to_schar = {
    "Zy": "!",
    "Qo": "@",
    "Jk": "+",
    "Vp": "-",
    "zy": "*",
    "qx": "/",
    "jp": "?",
    "vf": "%",
    "XF": "_",
    "QX": "=",
    "BX": ",",
    "VQ": ".",
    "JZ": ' " ',
    "ZQ": " ' ",
    "XY": "(",
    "RJ": ")",
    "VD": "#",
    "WN": "$"
}


def codes_table1(char):
    table = {
        11: "A", 21: "B", 31: "C", 41: "D", 51: "E", 61: "F", 71: "G", 81: "H",
        12: "I", 22: "J", 32: "K", 42: "L", 52: "M", 62: "N", 72: "O", 82: "P",
        13: "Q", 23: "R", 33: "S", 43: "T", 53: "U", 63: "V", 73: "W", 83: "X",
        14: "Y", 24: "Z", 34: "a", 44: "b", 54: "c", 64: "d", 74: "e", 84: "f",
        15: "g", 25: "h", 35: "i", 45: "j", 55: "k", 65: "l", 75: "m", 85: "n",
        16: "o", 26: "p", 36: "q", 46: "r", 56: "s", 66: "t", 76: "u", 86: "v",
        17: "w", 27: "x", 37: "y", 47: "z",
    }
    return table[char]


def decoding(text):
    text, finished_text = text, ""
    for symbol in list(map(int, text.split())):
        finished_text += codes_table1(symbol)
    return finished_text


def originalText(cipher_text, key):
    orig_text = []
    for i in range(len(cipher_text)):
        x = (test_list.index(cipher_text[i]) - test_list.index(key[i]) + 52) % 52
        orig_text.append(test_list[x])
    return ("".join(orig_text))


def convert_words_to_digits(string):
    for word, number in word_to_digit.items():
        string = string.replace(word, number)
    return string


def wordtospecialchar(message):
    converted_message = message
    for word, schar in word_to_schar.items():
        converted_message = converted_message.replace(word, schar)
    return converted_message


# This function is for calling all encryption function
keyword = "wrWejcknRRaiwHbnsebfLVLzOOduKrS"
def encode(string):
    if any(char in special_chars for char in string):
        string = specialchartoword(string)


    if any(char.isdigit() for char in string):
        string = convert_digits_to_words(string)
        string = string.replace(" ", "qi")
        key = generateKey(string, keyword)
        vigenere_encoding = cipherText(string, key)
        polybius_encoding = encoding(vigenere_encoding)
        return polybius_encoding

    else:
        string = string.replace(" ", "qi")
        key = generateKey(string, keyword)
        vigenere_encoding = cipherText(string, key)
        polybius_encoding = encoding(vigenere_encoding)
        return polybius_encoding



# This function is for calling all decryption function
def decode(string):
    polybius_decoding = decoding(string)
    key = generateKey(polybius_decoding, keyword)
    vigenere_decoding = originalText(polybius_decoding,key)
    vigenere_decoding = vigenere_decoding.replace("qi", " ")
    if any(key in vigenere_decoding for key in word_to_digit):
        vigenere_decoding = convert_words_to_digits(vigenere_decoding)
    if any(key in vigenere_decoding for key in word_to_schar):
        vigenere_decoding = wordtospecialchar(vigenere_decoding)
    return vigenere_decoding



