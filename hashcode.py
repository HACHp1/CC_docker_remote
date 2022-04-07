def hashCode(string):
    h = 0

    for i in string:
        h = 31 * h + ord(i)

    return h

print(hashCode('yx'))
print(hashCode('zY'))
