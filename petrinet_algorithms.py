from xml.dom import minidom
import collections


class PetriNet:
    """
    Using reachability graph to serach all possible paths of a petri net, with constrains that all loops only go around one time.
    express each state as a vertex,
    for e.g. (1, 0, 0, 0, 0), each 1 represent a place that current state holds
    for concurrent situations, the vertex gotta to be like (1, 0, 1, 0, 0).
    """
    def __init__(self, filename):
        self.place_in = collections.defaultdict(list)
        self.place_out = collections.defaultdict(list)
        self.tran_in = collections.defaultdict(list)
        self.tran_out = collections.defaultdict(list)
        self.trans2str = collections.defaultdict(str)
        self.places2str = collections.defaultdict(str)

        root = minidom.parse(filename).documentElement
        places = {}
        for i, p in enumerate(root.getElementsByTagName('place')):
            places[p.getAttribute('id')] = i
            self.places2str[i] = p.getElementsByTagName('text')[0].firstChild.data

        trans = {}
        for i, t in enumerate(root.getElementsByTagName('transition')):
            trans[t.getAttribute('id')] = i
            self.trans2str[i] = t.getElementsByTagName('text')[0].firstChild.data

        for arc in root.getElementsByTagName('arc'):
            s, e = arc.getAttribute('source'), arc.getAttribute('target')
            if s in places and e in trans:
                self.place_out[places[s]].append(trans[e])
                self.tran_in[trans[e]].append(places[s])
            elif s in trans and e in places:
                self.tran_out[trans[s]].append(places[e])
                self.place_in[places[e]].append(trans[s])
            else:
                print('Error, arc not correct')
                return

        # get start and end state
        self.start = tuple([[0, 1][not self.place_in[i]] for i in range(len(self.places2str))])
        self.end = tuple([[0, 1][not self.place_out[i]] for i in range(len(self.places2str))])

    def get_follow_states(self, cur):
        """
        get follow states that can be actived from cur state
        :param cur: tuple, cur state
        :return: {tran_index: state} pair
        """
        nx_state = collections.defaultdict(tuple)
        for i, val in enumerate(cur):
            if val <= 0:
                continue
            for t in self.place_out[i]:
                actived = [[cur[j], cur[j] - 1][j in self.tran_in[t]] for j in range(len(cur))]
                if all(val >= 0 for val in actived):
                    nx_state[t] = tuple([actived[j] + 1 if j in self.tran_out[t] else actived[j] for j in range(len(actived))])
        return nx_state

    def get_state_dis(self):
        """
        dfs search graph, get all state distance to end state,
        state in loops get dis of float('inf')
        :return: map, distance map
        """
        dis = {}
        def dfs(cur):
            if cur == self.end:
                return 0
            nx_state = self.get_follow_states(cur)
            dis[cur] = float('inf')
            for ns in nx_state.values():
                if ns not in dis:
                    dis[cur] = min(dis[cur], 1+dfs(ns))
            return dis[cur]
        dfs(self.start)
        dis[self.end] = 0
        return dis

    def get_all_path(self):
        """
        dfs search graph
        :return: None
        """
        paths = []
        dis = self.get_state_dis()

        def reach_graph(cur, path, visited):
            """
            :param cur: tuple, cur state
            :param path: list[int], visited trans
            :param visited: {tuple} visited loop
            :return:
            """
            if cur == self.end:
                paths.append(path[:])
                return
            nx_state = self.get_follow_states(cur)
            for t, ns in nx_state.items():
                is_loop = dis[ns] == float('inf') or dis[ns] >= dis[cur]
                # only visit loop once
                if is_loop and ns in visited:
                    continue
                if is_loop:
                    visited.add(ns)
                    reach_graph(ns, path + [t], visited)
                    visited.discard(ns)
                else:
                    reach_graph(ns, path + [t], visited)

        reach_graph(self.start, [], set())
        self.write_paths(paths)

    def write_paths(self, paths):
        """
        write path to file and print on console
        :param paths: list[int]
        :return:
        """
        log_file = 'log.txt'
        with open(log_file, 'w', encoding='utf-8') as f:
            for path in paths:
                s = ' '.join(self.trans2str[p] for p in path)
                f.write(s+'\n')
                print(s)


if __name__ == '__main__':
    file = '/Users/loick/Documents/研一/高级算法/结课作业/附件1/Model2.pnml'
    net = PetriNet(file)
    net.get_all_path()



