#pragma warning(disable: 4996)

#define _CRT_SECURE_NO_WARNINGS

#include <iostream>
#include <fstream>
#include <cstring>
#include <algorithm>
#include <vector>
#include <string>
#include <sstream>
#include <cstdlib>
using namespace std;


using Entry = pair<int, int>;

#define key first 
#define value second
#define ptr second
#define nonleaf 1
#define leaf 0


class FileHeader {//file���� �տ� 12����Ʈ�� �����ϴ� class 
public:

	int block_size = 0;
	int root_bid = 0;
	int depth = 0;
	char* file_name; //btree.bin
	int block_entry = 0;

	FileHeader(char* store_file_name) {
		this->file_name = store_file_name;
		readFileHeader(store_file_name);
	}

	FileHeader(char* store_fileName, int block_size)
	{
		this->block_size = block_size;
		this->block_entry = block_size / sizeof(int);
		this->file_name = store_fileName;
		ofstream out(file_name, ios::out);
		writeFileHeader(out);
		out.close();
	}

	int getBlockEntry() {
		return this->block_entry;
	}
	int getBlockSize() {
		return this->block_size;
	}

	void setFileHeader(int  block_size, int rootbid, int depth) { //file header setting 
		this->root_bid = root_bid;
		this->block_size = block_size;
		this->block_entry = block_size / sizeof(int);
		this->depth = depth;
	}

	void writeFileHeader(ofstream& out) { //������ ����� ������ ���Ͽ� write 
		out.seekp(0, ios::beg);
		out.write((char*)&block_size, sizeof(block_size));

		out.write((char*)&root_bid, sizeof(root_bid));

		out.write((char*)&depth, sizeof(depth));
	}
	void readFileHeader(char* fileName) {//������ �о ������ ����.  
		ifstream in(fileName, ios::binary);
		in.seekg(0, ios::beg);
		in.read((char*)&block_size, sizeof(block_size));
		this->block_entry = (block_size / sizeof(int));
		in.read((char*)&root_bid, sizeof(root_bid));
		in.read((char*)&depth, sizeof(depth));
		in.close();
	}

};


class BP_Tree {
public:

	FileHeader fileheader;//��� ���� ���� class 
	char* filename; // btree.bin
	vector<int> bid_list;//insert�ҽ� bid_list ���� ���� 


	BP_Tree(char* fileName)
		: filename(fileName), fileheader(fileName) {}

	void read_Block(int* block, int blockId) {//block�� blockID�� �ش��ϴ� block�� �д´�. 
		ifstream in(filename, ios::binary); 
		int loc = ((blockId - 1) * fileheader.getBlockSize()) + 12;
		in.seekg(loc, ios::beg);
		in.read((char*)block, fileheader.block_size);
		in.close();
	}

	void write_block(int* block, int blockId) {//blockid �� �־����� �� �ش� blockid block�� �д´�.
		ofstream out3(filename, ios::binary | ios::out | ios::in);
		int loc = (((blockId - 1) * fileheader.getBlockSize()) + 12);
		out3.seekp(loc, ios::beg);
		out3.write((char*)block, fileheader.getBlockSize());
		out3.close();
	}



	void insert(int key, int value) {

		int* block = new int[fileheader.getBlockEntry()];

		//�Ʒ� ifelse���� root block �� �д´�.	

		if (fileheader.root_bid == 0) {
			memset(block, 0, fileheader.getBlockEntry() * sizeof(int));
			fileheader.root_bid = 1;

			ofstream out(filename, ios::binary | ios::in | ios::out);
			fileheader.writeFileHeader(out);
			out.close();

		}

		else {
			read_Block(block, fileheader.root_bid);

		}
		Entry node = insert(block, fileheader.root_bid, key, value, 0);


		if (node.key != 0) {//��Ʈ��� ���ø��� �Ͼ�� setting 

			set_rootnode(node.key, node.ptr);

		}


	}

	Entry insert(int* block, int block_id, int key, int value, int depth) {//dfs ���� 
		//leaf��忡 ������ ��� leaf��忡 insert
		if (depth == fileheader.depth) {//��
			if (block[fileheader.getBlockEntry() - 3] == 0) {//leaf node�� ������  ������ ������ 

				insert_leafnode(block, key, value);
				write_block(block, block_id);
				delete[] block;

				return { 0,0 };
			}
			else {// split leaf node
				int* next_block = new int[fileheader.getBlockEntry()];
				memset(next_block, 0, fileheader.getBlockEntry() * sizeof(int));

			
				//**********************************************
				int idx = (((fileheader.getBlockEntry() * 4 + 4) / 8) - 1);
				idx /= 2;
				if ((((fileheader.getBlockEntry()*4 + 4) / 8) % 2) != 0) {
					idx--;
				}

				idx = (idx  * 2);
				//block�� key�� value�� next_block ���� �ű��.
				if (block[idx] > key) {
					
				
					for (int i = idx; i < fileheader.getBlockSize() / 4 - 1; i++) {
						next_block[i-idx] = block[i];
						block[i] = 0;
					
					}
					//key���� �����ؼ� insert 
					insert_leafnode(block, key, value);
				}
				else {
					for (int i = idx + 2; i < fileheader.getBlockSize() / 4 - 1; i++) {
						next_block[i-idx-2] = block[i];
						block[i] = 0;
					}
					//key���� �����ؼ� insert 
					insert_leafnode(next_block, key, value);
				}

				//**********************************************
				next_block[fileheader.getBlockEntry() - 1] = block[fileheader.getBlockEntry() - 1];
				//���� �����״� bid���� 
				int next_block_bid = getNewBID();		//���ο� bid�� �����´� .
				block[fileheader.getBlockEntry() - 1] = next_block_bid;

				/*	cout << "insert_leafnode after:" << endl;
					for (int i = 0; i < 9; i++) {
						cout << "block:" << block[i] << endl;
					}
					for (int i = 0; i < 9; i++) {
						cout << "next_block:" << next_block[i] << endl;
					}*/
					//cout << block_id << endl;
					//cout << next_block_bid;

				write_block(block, block_id);//���Ͽ� ���� 
				write_block(next_block, next_block_bid);

				return{ next_block[0], next_block_bid };

			}
		}

		//�ڽ� ��忡 insert

		int* childBlock = new int[fileheader.getBlockEntry()];
		int childBid = search_block(block, key);	//�ڽ� ��� bid
		read_Block(childBlock, childBid);

		Entry node = insert(childBlock, childBid, key, value, depth + 1);
		//�ڽ� ��尡 split ���� ���� ���

		if (node.key == 0) {

			return { 0,0 };
		}

		//�ڽĳ�忡�� split�� �Ͼ ���
		else {

			//index insert 
			return insertNonLeafNode(block, block_id, node.key, node.ptr);
		}
	}

	Entry insertNonLeafNode(int* block, int block_id, int key, int ptr) {
		//��尡 ������ ����� split �߻� 
		if (block[fileheader.getBlockEntry() - 1] != 0) {
			int* newBlock = new int[fileheader.getBlockEntry()];
			return split_NonLeafNode(block, block_id, newBlock, key, ptr);
		}
		//split�� �߻� x 
		else {
			insert_nonleafnode(block, key, ptr);
			write_block(block, block_id);
			return{ 0, 0 };
		}
	}

	Entry split_NonLeafNode(int* block, int block_id, int* newblock, int key, int ptr) {
		memset(newblock, 0, sizeof(int) * fileheader.getBlockEntry());

		int mid = ((fileheader.getBlockEntry() * 4 + 4) / 8) / 2;
		mid =mid* 2 - 1;

		if (key < block[mid]) {
		//block�� key�� value�� next_block ���� �ű��.
		
			for (int i = mid + 1; i < fileheader.getBlockEntry(); i++) {
				newblock[i-(mid + 1)] = block[i];
				block[i] = 0;
			}
		
			
			int mid_key = block[mid];
			block[mid] = 0;

			insert_nonleafnode(block, key, ptr);
			int newblock_bid = getNewBID();
			write_block(block, block_id);
			write_block(newblock, newblock_bid);
			return{ mid_key,  newblock_bid };
		}

		else {//insert�� entry�� b2�� �Ǿ��ϰ�� insert
			if (key < block[mid + 2]) {
				newblock[0] = ptr;
	
				for (int i = mid + 2; i < fileheader.getBlockSize() / 4; i++) {
					newblock[i-(mid + 1)] = block[i];
					block[i] = 0;
				}
			
				write_block(block, block_id);
				int newblock_bid = getNewBID();

				write_block(newblock, newblock_bid);
				return{ key, newblock_bid };
			}
			else {////insert�� entry�� b2�� �Ǿ��� �ƴҰ�� insert 
				
	
				for (int i = mid + 3; i < fileheader.getBlockEntry(); i++) {
					newblock[i- (mid + 3)] = block[i];
					block[i] = 0;
				}

				insert_nonleafnode(newblock, key, ptr);

				int ret_key = block[mid + 2];
				block[mid + 2] = 0;

				write_block(block, block_id);
				int newblock_bid = getNewBID();
				write_block(newblock, newblock_bid);
				return{ ret_key,newblock_bid };
			}
		}
	}










	void dfs_nonleafinsert(int key, int ptr) {

		bid_list.pop_back();
		int parent_bid = bid_list[bid_list.size() - 1];	//prev_bid= �����ִ� nonleafnode�� bid 		

		if (parent_bid == fileheader.root_bid) {
			set_rootnode(key, ptr);
			return;
		}

		int* block = new int[fileheader.getBlockEntry()];

		memset(block, 0, (fileheader.getBlockEntry() * sizeof(int)));
		read_Block(block, parent_bid);

		if (block[fileheader.getBlockEntry() - 2] == 0) {//leaf��� �� �־ split�ϰ� ���� �÷����� nonleafnode���� split�� �Ͼ�� ������ 

			insert_nonleafnode(block, key, ptr);
			write_block(block, parent_bid);
			delete[] block;
			return;
		}

		else {	//leaf��� �� �־ split�ϰ� ���� �÷����� non leaf node���� split �� �Ͼ� ���� 
			Entry E = split_nonleaf(block, key, ptr, parent_bid);
			if (E.key != 0) {
				dfs_nonleafinsert(E.key, E.ptr);
			}
			else {
				insert_nonleafnode(block, key, ptr);
				write_block(block, parent_bid);
				delete[] block;
				return;
			}
		}
	}




		Entry split_nonleaf(int* block, int key, int ptr, int bid) {
		int* next_block = new int[fileheader.getBlockEntry()];
		memset(next_block, 0, fileheader.getBlockEntry() * sizeof(int));


		int idx = ((fileheader.getBlockSize() + 4) / 8) / 2 * 2 - 1;
		if (key < block[idx]) {

			for (int i = idx + 1; i < fileheader.getBlockEntry(); i++) {
				next_block[i - (idx + 1)] = block[i];
				block[i] = 0;
			}

			int mid = block[idx];
			block[idx] = 0;

			insert_nonleafnode(block, key, ptr);
			int next_block_bid = getNewBID();

			write_block(block, bid);
			write_block(next_block, next_block_bid);

			return { mid,next_block_bid };
		}
		else {
			if (key < block[idx + 2]) {
				next_block[0] = ptr;
				for (int i = idx + 2; i < fileheader.getBlockEntry(); i++) {
					next_block[i - (idx + 2)] = block[i];
					block[i] = 0;
				}
				int next_block_bid = getNewBID();
				write_block(block, bid);
				write_block(next_block, next_block_bid);
				return{ key,  next_block_bid };
			}
			else {
				for (int i = idx + 3; i < fileheader.getBlockEntry(); i++) {
					next_block[i - (idx + 3)] = block[i];
					block[i] = 0;
				}
				insert_nonleafnode(next_block, key, ptr);
				int mid = block[idx + 2];
				block[idx + 2] = 0;

				int next_block_bid = getNewBID();
				write_block(block, bid);
				write_block(next_block, next_block_bid);
				return{ mid, next_block_bid };
			}


		}
	}





	//��Ʈ ��� ���ø��� �Ͼ ��� ��Ʈ ��带 ������Ʈ �Ѵ� 
	void set_rootnode(int key, int ptr) {

		int* root_block = new int[fileheader.getBlockEntry()];

		memset(root_block, 0, fileheader.getBlockEntry() * sizeof(int));

		fileheader.depth += 1;
		root_block[0] = fileheader.root_bid;
		root_block[1] = key;
		root_block[2] = ptr;

		fileheader.root_bid = getNewBID();


		ofstream out3(filename, ios::binary | ios::in | ios::out);
		fileheader.writeFileHeader(out3);
		out3.close();

		write_block(root_block, fileheader.root_bid);

		delete[] root_block;
	}

	//leafnode���� insert�ҋ� sort��ä�� �ֱ� 
	void insert_leafnode(int* block, int key, int value) {
		int idx = 0;
		while (block[idx] < key && block[idx] != 0) {
			idx += 2;
		}

		for (int i = fileheader.getBlockEntry() - 5; i >= idx; i -= 2) {
			block[i + 2] = block[i];
			block[i + 3] = block[i + 1];
		}

		block[idx] = key;
		block[idx + 1] = value;
	}

	//nonleaf node���� insert�ҋ� sort��ä�� �ֱ� 
	void insert_nonleafnode(int* block, int key, int ptr) {
		int idx = 1;
		while (block[idx] < key && block[idx] != 0) {
			idx += 2;
		}

		for (int i = fileheader.getBlockEntry() - 4; i >= idx; i -= 2) {
			block[i + 2] = block[i];
			block[i + 3] = block[i + 1];
		}

		block[idx] = key;
		block[idx + 1] = ptr;
	}


	//block ���� ���� �� bid ã�� �Լ� 
	int search_block(int* block, int key) {
		int i = 1;

		while (block[i] <= key && block[i] != 0) {

			i =i+ 2;

			if (i >= fileheader.getBlockEntry() - 1) {
				return block[fileheader.block_entry - 1];
			} //nonleaf���� �� ������ ��� return;
		}
		return block[i - 1];
	}

	//���� ä������ �ʴ� ���� bid�� return 
	int getNewBID() {
		ifstream in3(filename, ios::binary); //�����Է� ��Ʈ�� 
		in3.seekg(0, ios::end);
		int end = in3.tellg();
		in3.close();
		return ((end - 12) / fileheader.getBlockSize()) + 1;

	}
	//level 1,2 ��� 
	void print(char* argv) {


		ofstream out(argv);

		int* root_block = new int[fileheader.getBlockEntry()];
		read_Block(root_block, fileheader.root_bid);

		out << "<0>" << endl;

		if (fileheader.depth == 0) {//ù�� == leaf node

			int i = 0;
			int j = 0;
			while (root_block[i] != 0) {
				if (j != 0) {
					out << ",";
				}

				out << root_block[i];
				i += 2;
				if (i >= fileheader.getBlockEntry() - 1) {
					break;
				}
				j++;
			}
			out << endl;
		}
		else {
			vector<int> bid_list;
			bid_list.push_back(root_block[0]);

			for (int i = 1; i < fileheader.getBlockEntry(); i += 2) {
				if (root_block[i] == 0) {
					break;
				}
				bid_list.push_back(root_block[i + 1]);
			}

			printNode(out, root_block, nonleaf);
			out << endl;
			out << "<1>" << endl;

			if (fileheader.depth == 1) {
				for (int i = 0; i < bid_list.size(); i++) {
					int* block = new int[fileheader.getBlockEntry()];
					read_Block(block, bid_list[i]);
					printNode(out, block, leaf);
					delete[] block;
				}

			}
			else {
				for (int i = 0; i < bid_list.size(); i++) {
					int* block = new int[fileheader.getBlockEntry()];
					read_Block(block, bid_list[i]);

					printNode(out, block, nonleaf);

					delete[] block;
				}



			}

			out << endl;

		}
		out.close();
	}
	//flag�� nonleaf�� leaf�Ŀ� ���� �־��� block�� key�� ��� 
	void printNode(ofstream& out, int* block, int flag) {
		if (flag == leaf) {
			int j = 0;
			while (block[j] != 0) {

				out << block[j];
				j += 2;
				if (j >= fileheader.getBlockEntry() - 1) {
					break;
				}
			}
		}
		else if (flag == nonleaf) {
			int j = 1;
			while (block[j] != 0) {
				if (j != 1) {
					out << ",";
				}
				out << block[j];
				j += 2;
				if (j >= fileheader.getBlockEntry() - 1) {
					break;
				}
			}
		}
	}
	//point search �Լ� ���� 
	void search(ofstream& out, int key) {


		int* block = new int[fileheader.getBlockEntry()];
		read_Block(block, fileheader.root_bid);

		for (int depth = 0; depth < fileheader.depth; depth++) {
			int bid = search_block(block, key);
			read_Block(block, bid);
		}


		int i = 0;
		while (block[i] <= key) {
			if (block[i] == key && i != fileheader.getBlockEntry() - 1) {
				out << key << "," << block[i + 1] << endl;
				break;
			}

			i += 2;

			if (i == (fileheader.getBlockEntry() - 1)) {
				break;
			} 
		}

	}
	//range search �Լ� ���� 
	void range_search(ofstream& out, int startkey, int endkey) {
		int* block = new int[fileheader.getBlockEntry()];
		read_Block(block, fileheader.root_bid);

		for (int depth = 0; depth < fileheader.depth; depth++) {
			int bid = search_block(block, startkey);
			read_Block(block, bid);
		}

		int idx = 0;
		for (; idx < fileheader.getBlockEntry() - 1; idx += 2) {
			if (block[idx] >= startkey) {
				break;
			}
		}



		while (true) {
			for (int i = idx; i < fileheader.getBlockEntry() - 1; i += 2) {

				if (block[i] >= startkey && block[i] <= endkey) {
					out << block[i] << "," << block[i + 1] << '\t';
					if (i == fileheader.getBlockEntry() - 3) {
						int next_bid = block[fileheader.getBlockEntry() - 1];
						read_Block(block, next_bid);
						break;
					}

				}
				else if (block[i] == 0) {
					int next_bid = block[fileheader.getBlockEntry() - 1];
					read_Block(block, next_bid);
					idx = 0;
					break;
				}

				else {
					out << "\n";
					return;
				}
			}

		}
	}
};

int main(int argc, char* argv[]) {

	char CM = argv[1][0];

	char* store_fileName = argv[2];

	switch (CM) {

	case 'c':
	case 'C': {//�����ڵ�: btree.exe c btree.bin 36
		FileHeader fh(store_fileName, atoi(argv[3]));
		break;
	}

	case 'i'://�����ڵ�:btree.exe i btree.bin sample_insertion_input.txt 
	case 'I':
	{

		BP_Tree BPTree(store_fileName);

		ifstream in2(argv[3]);

		char string[50];

		while (in2 >> string) {
			int key = atoi(strtok(string, ","));
			int value = atoi(strtok(NULL, "\n"));

			BPTree.insert(key, value);

		}
		in2.close();

		break;
	}

	case 'p':
	case 'P':
	{
		BP_Tree BPTree(store_fileName);

		BPTree.print(argv[3]);

		break;
	}

	case 's':
	case 'S':
	{
		BP_Tree BPTree(store_fileName);
		ifstream in_s(argv[3]);
		ofstream out_s(argv[4]);

		int key;

		while (in_s >> key) {

			BPTree.search(out_s, key);
		}

		in_s.close();
		out_s.close();
		break;
	}

	case 'r':
	case 'R':
	{
		BP_Tree BPTree(store_fileName);
		ifstream inr(argv[3]);
		ofstream outr(argv[4]);

		char key[30];


		while (inr >> key) {
			int startkey = atoi(strtok(key, ","));
			int endkey = atoi(strtok(NULL, "\n"));
			BPTree.range_search(outr, startkey, endkey);
		}

		inr.close();
		outr.close();

		break;
	}
	}

}