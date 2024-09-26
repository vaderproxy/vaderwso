import random

def wso_query(url, code):
    result = {
        "url":url,
        "cookie":{"test":"test"},
        "post":{"c":code}
    }

    return result
