def all_paths(expression, aggregated_result = []):
    result = [aggregated_result]

    if isinstance(expression, tuple):
        for i, x in enumerate(expression):
            if i > 0:
                result += all_paths(x, aggregated_result + [i])

    return result

def get_in(expression, path):
    if len(path) == 0:
        return expression
    else:
        return get_in(expression[path[0]], path[1:])

def assoc_in(expression, path, new_value):
    if len(path) == 0:
        return new_value
    else:
        return tuple(assoc_in(x, path[1:], new_value) if path[0] == i else x for i, x in enumerate(expression))
