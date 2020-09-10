import sys

# input_file = "T10I4D100K"
# input_file = "T40I10D100K"
# input_file = "kosarak"

input_file = sys.argv[1]

dic = {}

# Read in data
with open(input_file+".txt") as f:
    for l in f:
        w = l.split()[0]
        if w in dic:
            dic[w] = dic[w] + 1
        else:
            dic[w] = 1

keys = []
values = []
for k, v in dic.items():
    keys.append(k)
    values.append(v)

# sort keys based on values
keys.sort(key=dict(zip(keys, values)).get)
values.sort()

print("Number of distinct elements: " + str(len(keys)))
print("Number of total elements: " + str(sum(values)))
print("Most frequent item: " + keys[len(keys)-1] + " with occurance: " + str(values[len(keys)-1]))
print("Least frequent item: " + keys[0] + " with occurance: " + str(values[0]))

output_file = input_file+"_frequency.txt"
output = open(output_file, "w")
for k, v in zip(keys, values):
    output.write(k)
    output.write(" ")
    output.write(str(v))
    output.write("\n")
output.close()

value_frequency_dict = {}
for v in values:
    if v in value_frequency_dict:
        value_frequency_dict[v] += 1
    else:
        value_frequency_dict[v] = 1

output_file = input_file+"_analysis.txt"
output = open(output_file, "w")
for k, v in value_frequency_dict.items():
    output.write(str(k))
    output.write(" ")
    output.write(str(v))
    output.write("\n")
output.close
